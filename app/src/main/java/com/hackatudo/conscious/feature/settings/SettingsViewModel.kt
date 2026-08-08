package com.hackatudo.conscious.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hackatudo.conscious.domain.repository.FocusSessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(val deleting: Boolean = false, val message: String? = null)

@HiltViewModel
class SettingsViewModel @Inject constructor(private val sessions: FocusSessionRepository) : ViewModel() {
    private val mutableState = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = mutableState
    fun deletePersonalHistory() = viewModelScope.launch {
        mutableState.update { it.copy(deleting = true, message = null) }
        runCatching { sessions.deleteAll() }
            .onSuccess { mutableState.value = SettingsUiState(message = "Histórico pessoal excluído. Onboarding e grupos foram mantidos.") }
            .onFailure { mutableState.value = SettingsUiState(message = "Não foi possível excluir agora.") }
    }
}
