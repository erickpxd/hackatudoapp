package com.hackatudo.conscious.feature.launcher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hackatudo.conscious.core.launcher.HomeRoleManager
import com.hackatudo.conscious.core.launcher.HomeRoleStatus
import com.hackatudo.conscious.domain.model.InstalledApp
import com.hackatudo.conscious.domain.repository.InstalledAppsRepository
import com.hackatudo.conscious.domain.model.FocusSession
import com.hackatudo.conscious.domain.usecase.session.GetCurrentSessionUseCase
import com.hackatudo.conscious.domain.model.AppLaunchEvaluation
import com.hackatudo.conscious.domain.usecase.intervention.EvaluateAppLaunchUseCase
import com.hackatudo.conscious.core.datastore.PrivacyPreferencesDataStore
import com.hackatudo.conscious.domain.usecase.insights.GetPersonalInsightsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

data class LauncherUiState(
    val isLoading: Boolean = true,
    val apps: List<InstalledApp> = emptyList(),
    val homeRoleStatus: HomeRoleStatus = HomeRoleStatus.NOT_SELECTED,
    val errorMessage: String? = null,
    val currentSession: FocusSession? = null,
    val userName: String = "",
    val petName: String = "neko",
    val sessionsToday: Int = 0,
    val studyMillisToday: Long = 0,
    val showHomeRolePrompt: Boolean = false,
)

sealed interface LauncherEffect {
    data class OpenApp(val packageName: String) : LauncherEffect
    data class ShowIntervention(val packageName: String) : LauncherEffect
}

@HiltViewModel
class LauncherViewModel @Inject constructor(
    private val installedAppsRepository: InstalledAppsRepository,
    private val homeRoleManager: HomeRoleManager,
    private val getCurrentSession: GetCurrentSessionUseCase,
    private val evaluateAppLaunch: EvaluateAppLaunchUseCase,
    private val getPersonalInsights: GetPersonalInsightsUseCase,
    private val preferences: PrivacyPreferencesDataStore,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(LauncherUiState())
    val uiState: StateFlow<LauncherUiState> = mutableUiState
    private val effectChannel = Channel<LauncherEffect>(Channel.BUFFERED)
    val effects = effectChannel.receiveAsFlow()

    init { load() }

    fun retry() = load()
    fun refreshHomeRole() = homeRoleManager.refresh()
    fun dismissHomeRolePrompt() = viewModelScope.launch { preferences.setHomeRolePromptDismissed(true) }
    fun requestAppLaunch(packageName: String) {
        viewModelScope.launch {
            val effect = when (evaluateAppLaunch(packageName)) {
                AppLaunchEvaluation.ALLOW -> LauncherEffect.OpenApp(packageName)
                AppLaunchEvaluation.INTERVENE -> LauncherEffect.ShowIntervention(packageName)
            }
            effectChannel.send(effect)
        }
    }

    private fun load() {
        viewModelScope.launch {
            runCatching {
                combine(installedAppsRepository.observeLaunchableApps(), homeRoleManager.status, getCurrentSession(), getPersonalInsights(), preferences.preferences) { apps, role, session, insights, profile ->
                    val today = System.currentTimeMillis() / 86_400_000L
                    val todayInsight = insights.daily.firstOrNull { it.epochDay == today }
                    LauncherUiState(
                        isLoading = false,
                        apps = apps,
                        homeRoleStatus = role,
                        currentSession = session,
                        userName = profile.userName,
                        petName = profile.petName,
                        sessionsToday = todayInsight?.sessionCount ?: 0,
                        studyMillisToday = todayInsight?.totalDurationMillis ?: 0,
                        showHomeRolePrompt = role == HomeRoleStatus.NOT_SELECTED && !profile.homeRolePromptDismissed,
                    )
                }.collect { mutableUiState.value = it }
            }.onFailure {
                mutableUiState.update { state ->
                    state.copy(isLoading = false, errorMessage = "Não foi possível listar os aplicativos.")
                }
            }
        }
    }
}
