package com.hackatudo.conscious.feature.session.active

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hackatudo.conscious.core.datastore.PrivacyPreferencesDataStore
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

data class ActiveSessionUiState(val session: FocusSession? = null, val remainingMillis: Long = 0, val petName: String = "Neko")

sealed interface ActiveSessionEffect {
    data class ShowSummary(val sessionId: java.util.UUID) : ActiveSessionEffect
    data object ReturnToLauncher : ActiveSessionEffect
}

@HiltViewModel
class ActiveSessionViewModel @Inject constructor(
    current: GetCurrentSessionUseCase,
    private val clock: Clock,
    private val pause: PauseFocusSessionUseCase,
    private val resume: ResumeFocusSessionUseCase,
    private val complete: CompleteFocusSessionUseCase,
    private val cancel: CancelFocusSessionUseCase,
    private val changeIntention: ChangeSessionIntentionUseCase,
    preferences: PrivacyPreferencesDataStore,
) : ViewModel() {
    private val effectChannel = Channel<ActiveSessionEffect>(Channel.BUFFERED)
    val effects = effectChannel.receiveAsFlow()
    private val ticker = flow {
        while (true) {
            emit(Unit)
            delay(1_000)
        }
    }
    val uiState: StateFlow<ActiveSessionUiState> = combine(current(), preferences.preferences, ticker) { session, profile, _ ->
        val elapsed = session?.startedAtEpochMillis?.let { startedAt ->
            val countingUntil = session.pauseStartedAtEpochMillis ?: clock.nowEpochMillis()
            (countingUntil - startedAt - session.accumulatedPauseMillis).coerceAtLeast(0)
        } ?: 0
        ActiveSessionUiState(session, (session?.plannedDurationMillis ?: 0) - elapsed, profile.petName)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ActiveSessionUiState())

    fun pause() = withSession { pause(it.id) }
    fun resume() = withSession { resume(it.id) }
    fun complete() = withSession { session ->
        complete(session.id)
        effectChannel.send(ActiveSessionEffect.ShowSummary(session.id))
    }
    fun cancel() = withSession { session ->
        cancel(session.id)
        effectChannel.send(ActiveSessionEffect.ReturnToLauncher)
    }
    fun changeIntention(value: String) = withSession { changeIntention(it.id, value) }
    private fun withSession(action: suspend (FocusSession) -> Unit) { viewModelScope.launch { uiState.value.session?.let { action(it) } } }
}
