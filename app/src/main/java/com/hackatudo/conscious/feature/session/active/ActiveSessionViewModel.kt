package com.hackatudo.conscious.feature.session.active

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hackatudo.conscious.core.time.Clock
import com.hackatudo.conscious.domain.model.FocusSession
import com.hackatudo.conscious.domain.usecase.session.CancelFocusSessionUseCase
import com.hackatudo.conscious.domain.usecase.session.ChangeSessionIntentionUseCase
import com.hackatudo.conscious.domain.usecase.session.CompleteFocusSessionUseCase
import com.hackatudo.conscious.domain.usecase.session.GetCurrentSessionUseCase
import com.hackatudo.conscious.domain.usecase.session.PauseFocusSessionUseCase
import com.hackatudo.conscious.domain.usecase.session.ResumeFocusSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ActiveSessionUiState(val session: FocusSession? = null, val remainingMillis: Long = 0)

@HiltViewModel
class ActiveSessionViewModel @Inject constructor(
    current: GetCurrentSessionUseCase,
    private val clock: Clock,
    private val pause: PauseFocusSessionUseCase,
    private val resume: ResumeFocusSessionUseCase,
    private val complete: CompleteFocusSessionUseCase,
    private val cancel: CancelFocusSessionUseCase,
    private val changeIntention: ChangeSessionIntentionUseCase,
) : ViewModel() {
    val uiState: StateFlow<ActiveSessionUiState> = current().map { session ->
        val elapsed = session?.startedAtEpochMillis?.let { (clock.nowEpochMillis() - it - session.accumulatedPauseMillis).coerceAtLeast(0) } ?: 0
        ActiveSessionUiState(session, (session?.plannedDurationMillis ?: 0) - elapsed)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ActiveSessionUiState())

    fun pause() = withSession { pause(it.id) }
    fun resume() = withSession { resume(it.id) }
    fun complete() = withSession { complete(it.id) }
    fun cancel() = withSession { cancel(it.id) }
    fun changeIntention(value: String) = withSession { changeIntention(it.id, value) }
    private fun withSession(action: suspend (FocusSession) -> Unit) { viewModelScope.launch { uiState.value.session?.let { action(it) } } }
}
