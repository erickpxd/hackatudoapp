package com.hackatudo.conscious.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hackatudo.conscious.core.datastore.PrivacyPreferencesDataStore
import com.hackatudo.conscious.domain.repository.FocusSessionRepository
import com.hackatudo.conscious.domain.repository.InstalledAppsRepository
import com.hackatudo.conscious.domain.model.InstalledApp
import com.hackatudo.conscious.core.launcher.HomeRoleManager
import com.hackatudo.conscious.core.launcher.HomeRoleStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val deleting: Boolean = false,
    val message: String? = null,
    val userName: String = "",
    val petName: String = "Neko",
    val themeColor: String = "blue",
    val notificationsEnabled: Boolean = true,
    val breakRemindersEnabled: Boolean = true,
    val currentContext: String = "school",
    val distractingPackages: Set<String> = emptySet(),
    val apps: List<InstalledApp> = emptyList(),
    val mutedNotificationPackages: Set<String> = emptySet(),
    val homeRoleStatus: HomeRoleStatus = HomeRoleStatus.NOT_SELECTED,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val sessions: FocusSessionRepository,
    private val preferences: PrivacyPreferencesDataStore,
    installedApps: InstalledAppsRepository,
    private val homeRoleManager: HomeRoleManager,
) : ViewModel() {
    private val mutableState = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = mutableState
    init {
        viewModelScope.launch {
            preferences.preferences.collect { profile ->
                mutableState.update { it.copy(
                    userName = profile.userName, petName = profile.petName, themeColor = profile.themeColor,
                    notificationsEnabled = profile.notificationsEnabled, breakRemindersEnabled = profile.breakRemindersEnabled,
                    currentContext = profile.currentContext, distractingPackages = profile.distractingPackages,
                    mutedNotificationPackages = profile.mutedNotificationPackages,
                ) }
            }
        }
        viewModelScope.launch { installedApps.observeLaunchableApps().collect { apps -> mutableState.update { it.copy(apps = apps) } } }
        viewModelScope.launch { homeRoleManager.status.collect { status -> mutableState.update { it.copy(homeRoleStatus = status) } } }
    }
    fun refreshHomeRole() = homeRoleManager.refresh()
    fun setThemeColor(value: String) = viewModelScope.launch { preferences.setThemeColor(value) }
    fun setNotificationsEnabled(value: Boolean) = viewModelScope.launch { preferences.setNotificationsEnabled(value) }
    fun setBreakRemindersEnabled(value: Boolean) = viewModelScope.launch { preferences.setBreakRemindersEnabled(value) }
    fun setCurrentContext(value: String) = viewModelScope.launch { preferences.setCurrentContext(value) }
    fun setDistracting(packageName: String, enabled: Boolean) = viewModelScope.launch {
        val base = mutableState.value.distractingPackages.toMutableSet()
        if (enabled) base += packageName else base -= packageName
        preferences.setDistractingPackages(base)
    }
    fun setAppNotifications(packageName: String, enabled: Boolean) = viewModelScope.launch {
        val muted = mutableState.value.mutedNotificationPackages.toMutableSet()
        if (enabled) muted -= packageName else muted += packageName
        preferences.setMutedNotificationPackages(muted)
    }
    fun deletePersonalHistory() = viewModelScope.launch {
        mutableState.update { it.copy(deleting = true, message = null) }
        runCatching { sessions.deleteAll() }
            .onSuccess { mutableState.update { it.copy(deleting = false, message = "Histórico pessoal excluído. Onboarding e grupos foram mantidos.") } }
            .onFailure { mutableState.update { it.copy(deleting = false, message = "Não foi possível excluir agora.") } }
    }

}
