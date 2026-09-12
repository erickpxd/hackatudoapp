package com.hackatudo.conscious.domain.usecase.session

import com.hackatudo.conscious.core.time.Clock
import com.hackatudo.conscious.domain.model.FocusSession
import com.hackatudo.conscious.domain.model.UsageIntent
import com.hackatudo.conscious.domain.repository.FocusSessionRepository
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class CreateFocusSessionUseCase @Inject constructor(private val repository: FocusSessionRepository, private val clock: Clock) {
    suspend operator fun invoke(title: String, durationMillis: Long, packages: Set<String>, groupId: UUID? = null): FocusSession {
        require(repository.observeCurrentValue() == null) { "Já existe uma sessão ativa" }
        val now = clock.nowEpochMillis()
        val sessionId = UUID.randomUUID()
        val session = FocusSession(
            id = sessionId,
            title = title.trim(),
            plannedDurationMillis = durationMillis,
            createdAtEpochMillis = now,
            intentions = listOf(UsageIntent(sessionId = sessionId, text = title.trim(), createdAtEpochMillis = now)),
            selectedPackageNames = packages,
            groupId = groupId,
        )
        repository.save(session)
        return session
    }
}

class StartFocusSessionUseCase @Inject constructor(private val repository: FocusSessionRepository, private val clock: Clock) {
    suspend operator fun invoke(id: UUID) = update(id) { it.start(clock.nowEpochMillis()) }
    private suspend fun update(id: UUID, block: (FocusSession) -> FocusSession): FocusSession = block(requireNotNull(repository.get(id))).also { repository.save(it) }
}

class PauseFocusSessionUseCase @Inject constructor(private val repository: FocusSessionRepository, private val clock: Clock) {
    suspend operator fun invoke(id: UUID) = requireNotNull(repository.get(id)).pause(clock.nowEpochMillis()).also { repository.save(it) }
}

class ResumeFocusSessionUseCase @Inject constructor(private val repository: FocusSessionRepository, private val clock: Clock) {
    suspend operator fun invoke(id: UUID) = requireNotNull(repository.get(id)).resume(clock.nowEpochMillis()).also { repository.save(it) }
}

class CompleteFocusSessionUseCase @Inject constructor(private val repository: FocusSessionRepository, private val clock: Clock) {
    suspend operator fun invoke(id: UUID) = requireNotNull(repository.get(id)).complete(clock.nowEpochMillis()).also { repository.save(it) }
}

class CancelFocusSessionUseCase @Inject constructor(private val repository: FocusSessionRepository, private val clock: Clock) {
    suspend operator fun invoke(id: UUID) = requireNotNull(repository.get(id)).cancel(clock.nowEpochMillis()).also { repository.save(it) }
}

class ChangeSessionIntentionUseCase @Inject constructor(private val repository: FocusSessionRepository, private val clock: Clock) {
    suspend operator fun invoke(id: UUID, intention: String) = requireNotNull(repository.get(id)).changeIntention(intention, clock.nowEpochMillis()).also { repository.save(it) }
}

class GetCurrentSessionUseCase @Inject constructor(private val repository: FocusSessionRepository) {
    operator fun invoke() = repository.observeCurrent()
}

private suspend fun FocusSessionRepository.observeCurrentValue(): FocusSession? = observeCurrent().first()
