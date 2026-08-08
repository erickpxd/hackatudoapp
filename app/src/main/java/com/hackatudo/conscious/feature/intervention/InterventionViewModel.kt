package com.hackatudo.conscious.feature.intervention

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hackatudo.conscious.core.time.Clock
import com.hackatudo.conscious.domain.model.InterventionDecision
import com.hackatudo.conscious.domain.model.ReflectionReason
import com.hackatudo.conscious.domain.repository.InstalledAppsRepository
import com.hackatudo.conscious.domain.usecase.intervention.RecordInterventionDecisionUseCase
import com.hackatudo.conscious.domain.usecase.intervention.RecordInterventionShownUseCase
import com.hackatudo.conscious.domain.usecase.session.GetCurrentSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class InterventionUiState(
    val isLoading: Boolean = false,
    val intention: String = "",
    val appName: String = "",
    val remainingMillis: Long = 0,
    val selectedReason: ReflectionReason? = null,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface InterventionEffect {
    data object ReturnToLauncher : InterventionEffect
    data class OpenApp(val packageName: String) : InterventionEffect
}

@HiltViewModel
class InterventionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    currentSession: GetCurrentSessionUseCase,
    installedApps: InstalledAppsRepository,
    private val clock: Clock,
    private val recordShown: RecordInterventionShownUseCase,
    private val recordDecision: RecordInterventionDecisionUseCase,
) : ViewModel() {
    private val packageName: String = checkNotNull(savedStateHandle["packageName"])
    private val currentSessionFlow = currentSession()
    private val formState = MutableStateFlow(InterventionUiState(isLoading = true))
    private val effectChannel = Channel<InterventionEffect>(Channel.BUFFERED)
    val effects = effectChannel.receiveAsFlow()
    private var shownRecorded = false

    val uiState: StateFlow<InterventionUiState> = combine(
        currentSessionFlow,
        installedApps.observeLaunchableApps(),
        formState,
    ) { session, apps, form ->
        if (session == null) {
            form.copy(isLoading = false, errorMessage = "A sessão não está mais ativa.")
        } else {
            if (!shownRecorded) {
                shownRecorded = true
                viewModelScope.launch { recordShown(session.id, packageName) }
            }
            val elapsed = session.startedAtEpochMillis
                ?.let { (clock.nowEpochMillis() - it - session.accumulatedPauseMillis).coerceAtLeast(0) }
                ?: 0
            form.copy(
                isLoading = false,
                intention = session.currentIntention?.text ?: session.title,
                appName = apps.firstOrNull { it.packageName == packageName }?.displayName ?: packageName,
                remainingMillis = (session.plannedDurationMillis - elapsed).coerceAtLeast(0),
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), InterventionUiState(isLoading = true))

    fun selectReason(reason: ReflectionReason?) {
        formState.update { it.copy(selectedReason = reason, errorMessage = null) }
    }

    fun stayFocused() = resolve(InterventionDecision.STAY_FOCUSED)

    fun openAnyway() = resolve(InterventionDecision.OPEN_ANYWAY)

    private fun resolve(decision: InterventionDecision) {
        if (formState.value.isSubmitting) return
        viewModelScope.launch {
            val session = uiState.value.takeUnless { it.isLoading || it.errorMessage != null }
            val current = session ?: return@launch
            formState.update { it.copy(isSubmitting = true, errorMessage = null) }
            runCatching {
                val activeSession = currentSessionId()
                recordDecision(activeSession, packageName, decision, formState.value.selectedReason)
            }.onSuccess {
                effectChannel.send(
                    if (decision == InterventionDecision.OPEN_ANYWAY) {
                        InterventionEffect.OpenApp(packageName)
                    } else {
                        InterventionEffect.ReturnToLauncher
                    },
                )
            }.onFailure {
                formState.update {
                    it.copy(isSubmitting = false, errorMessage = "Não foi possível registrar sua escolha. Tente novamente.")
                }
            }
        }
    }

    private suspend fun currentSessionId() = checkNotNull(currentSessionFlow.first()?.id)
}
