package com.hackatudo.conscious.feature.launcher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hackatudo.conscious.core.launcher.HomeRoleManager
import com.hackatudo.conscious.core.launcher.HomeRoleStatus
import com.hackatudo.conscious.domain.model.InstalledApp
import com.hackatudo.conscious.domain.repository.InstalledAppsRepository
import com.hackatudo.conscious.domain.model.FocusSession
import com.hackatudo.conscious.domain.usecase.session.GetCurrentSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LauncherUiState(
    val isLoading: Boolean = true,
    val apps: List<InstalledApp> = emptyList(),
    val homeRoleStatus: HomeRoleStatus = HomeRoleStatus.NOT_SELECTED,
    val errorMessage: String? = null,
    val currentSession: FocusSession? = null,
)

@HiltViewModel
class LauncherViewModel @Inject constructor(
    private val installedAppsRepository: InstalledAppsRepository,
    private val homeRoleManager: HomeRoleManager,
    private val getCurrentSession: GetCurrentSessionUseCase,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(LauncherUiState())
    val uiState: StateFlow<LauncherUiState> = mutableUiState

    init { load() }

    fun retry() = load()
    fun refreshHomeRole() = homeRoleManager.refresh()

    private fun load() {
        viewModelScope.launch {
            runCatching {
                combine(installedAppsRepository.observeLaunchableApps(), homeRoleManager.status, getCurrentSession()) { apps, role, session ->
                    LauncherUiState(isLoading = false, apps = apps, homeRoleStatus = role, currentSession = session)
                }.collect { mutableUiState.value = it }
            }.onFailure {
                mutableUiState.update { state ->
                    state.copy(isLoading = false, errorMessage = "Não foi possível listar os aplicativos.")
                }
            }
        }
    }
}
