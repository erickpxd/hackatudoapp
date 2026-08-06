package com.hackatudo.conscious.feature.session.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hackatudo.conscious.domain.model.ContentSource
import com.hackatudo.conscious.domain.model.FocusContext
import com.hackatudo.conscious.domain.model.InstalledApp
import com.hackatudo.conscious.domain.repository.FocusContextRepository
import com.hackatudo.conscious.domain.repository.InstalledAppsRepository
import com.hackatudo.conscious.domain.usecase.session.CreateFocusSessionUseCase
import com.hackatudo.conscious.domain.usecase.session.StartFocusSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CreateSessionUiState(
    val intention: String = "",
    val durationMinutes: Int = 30,
    val selectedPackages: Set<String> = emptySet(),
    val apps: List<InstalledApp> = emptyList(),
    val contexts: List<FocusContext> = emptyList(),
    val error: String? = null,
    val started: Boolean = false,
)

@HiltViewModel
class CreateSessionViewModel @Inject constructor(
    installedAppsRepository: InstalledAppsRepository,
    private val contextRepository: FocusContextRepository,
    private val createSession: CreateFocusSessionUseCase,
    private val startSession: StartFocusSessionUseCase,
) : ViewModel() {
    private val mutableState = MutableStateFlow(CreateSessionUiState())
    val uiState: StateFlow<CreateSessionUiState> = mutableState

    init {
        viewModelScope.launch {
            combine(installedAppsRepository.observeLaunchableApps(), contextRepository.observeAll()) { apps, contexts -> apps to contexts }
                .collect { (apps, contexts) -> mutableState.update { it.copy(apps = apps, contexts = contexts) } }
        }
    }

    fun setIntention(value: String) = mutableState.update { it.copy(intention = value, error = null) }
    fun setDuration(value: Int) = mutableState.update { it.copy(durationMinutes = value.coerceAtLeast(1), error = null) }
    fun toggleApp(packageName: String) = mutableState.update {
        it.copy(selectedPackages = if (packageName in it.selectedPackages) it.selectedPackages - packageName else it.selectedPackages + packageName)
    }
    fun applyContext(context: FocusContext) = mutableState.update {
        it.copy(intention = context.suggestedIntention, selectedPackages = context.relatedPackageNames)
    }
    fun saveContext(name: String) = viewModelScope.launch {
        val state = mutableState.value
        if (name.isNotBlank() && state.intention.isNotBlank()) {
            contextRepository.save(FocusContext(name = name.trim(), suggestedIntention = state.intention.trim(), relatedPackageNames = state.selectedPackages, source = ContentSource.STUDENT))
        }
    }
    fun deleteContext(context: FocusContext) = viewModelScope.launch { if (context.source == ContentSource.STUDENT) contextRepository.delete(context) }
    fun start() = viewModelScope.launch {
        val state = mutableState.value
        if (state.intention.isBlank()) {
            mutableState.update { it.copy(error = "Defina sua intenção antes de começar.") }
            return@launch
        }
        runCatching {
            createSession(state.intention, state.durationMinutes * 60_000L, state.selectedPackages)
        }.mapCatching { startSession(it.id) }
            .onSuccess { mutableState.update { it.copy(started = true) } }
            .onFailure { mutableState.update { it.copy(error = "Não foi possível iniciar a sessão.") } }
    }
}
