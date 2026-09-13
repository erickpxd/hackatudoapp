package com.hackatudo.conscious.feature.summary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hackatudo.conscious.domain.model.PersonalSummary
import com.hackatudo.conscious.domain.repository.FocusSessionRepository
import com.hackatudo.conscious.domain.usecase.summary.BuildSessionSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SessionSummaryUiState(
    val isLoading: Boolean = true,
    val summary: PersonalSummary? = null,
    val errorMessage: String? = null,
)

sealed interface SessionSummaryEffect {
    data object HistoryDeleted : SessionSummaryEffect
}

@HiltViewModel
class SessionSummaryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val buildSummary: BuildSessionSummaryUseCase,
    private val sessions: FocusSessionRepository,
) : ViewModel() {
    private val sessionId = UUID.fromString(checkNotNull(savedStateHandle["sessionId"]))
    private val mutableState = MutableStateFlow(SessionSummaryUiState())
    val uiState: StateFlow<SessionSummaryUiState> = mutableState
    private val effectChannel = Channel<SessionSummaryEffect>(Channel.BUFFERED)
    val effects = effectChannel.receiveAsFlow()

    init {
        load()
    }

    fun retry() = load()

    fun deleteSession() = delete { sessions.delete(sessionId) }

    fun deleteAllHistory() = delete { sessions.deleteAll() }

    private fun load() {
        viewModelScope.launch {
            mutableState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching { buildSummary(sessionId) }
                .onSuccess { summary -> mutableState.value = SessionSummaryUiState(isLoading = false, summary = summary) }
                .onFailure {
                    mutableState.value = SessionSummaryUiState(
                        isLoading = false,
                        errorMessage = "Não foi possível carregar o resumo.",
                    )
                }
        }
    }

    private fun delete(action: suspend () -> Unit) {
        viewModelScope.launch {
            runCatching { action() }
                .onSuccess { effectChannel.send(SessionSummaryEffect.HistoryDeleted) }
                .onFailure { mutableState.update { it.copy(errorMessage = "Não foi possível excluir o histórico.") } }
        }
    }
}
