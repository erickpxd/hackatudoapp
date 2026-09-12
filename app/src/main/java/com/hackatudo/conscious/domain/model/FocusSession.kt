package com.hackatudo.conscious.domain.model

import java.util.UUID

enum class FocusSessionStatus { PLANNED, ACTIVE, PAUSED, COMPLETED, CANCELLED }
enum class ContentSource { STUDENT, TEACHER, INSTITUTION }

data class UsageIntent(
    val id: UUID = UUID.randomUUID(),
    val sessionId: UUID,
    val text: String,
    val source: ContentSource = ContentSource.STUDENT,
    val createdAtEpochMillis: Long,
    val activeUntilEpochMillis: Long? = null,
)

data class FocusContext(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val suggestedIntention: String,
    val relatedPackageNames: Set<String> = emptySet(),
    val source: ContentSource = ContentSource.STUDENT,
)

data class FocusSession(
    val id: UUID = UUID.randomUUID(),
    val title: String,
    val plannedDurationMillis: Long,
    val createdAtEpochMillis: Long,
    val status: FocusSessionStatus = FocusSessionStatus.PLANNED,
    val startedAtEpochMillis: Long? = null,
    val endedAtEpochMillis: Long? = null,
    val accumulatedPauseMillis: Long = 0,
    val pauseStartedAtEpochMillis: Long? = null,
    val intentions: List<UsageIntent> = emptyList(),
    val selectedPackageNames: Set<String> = emptySet(),
    val groupId: UUID? = null,
) {
    init {
        require(title.isNotBlank())
        require(plannedDurationMillis > 0)
        require(accumulatedPauseMillis >= 0)
    }

    val currentIntention: UsageIntent? get() = intentions.lastOrNull { it.activeUntilEpochMillis == null }

    fun start(now: Long) = transition(FocusSessionStatus.ACTIVE, now)
    fun pause(now: Long) = transition(FocusSessionStatus.PAUSED, now)
    fun resume(now: Long): FocusSession {
        require(status == FocusSessionStatus.PAUSED)
        val pause = pauseStartedAtEpochMillis ?: now
        return copy(
            status = FocusSessionStatus.ACTIVE,
            accumulatedPauseMillis = accumulatedPauseMillis + (now - pause).coerceAtLeast(0),
            pauseStartedAtEpochMillis = null,
        )
    }
    fun complete(now: Long) = finish(FocusSessionStatus.COMPLETED, now)
    fun cancel(now: Long) = finish(FocusSessionStatus.CANCELLED, now)

    fun changeIntention(text: String, now: Long): FocusSession {
        require(status == FocusSessionStatus.ACTIVE || status == FocusSessionStatus.PAUSED)
        require(text.isNotBlank())
        val closed = intentions.map { if (it.activeUntilEpochMillis == null) it.copy(activeUntilEpochMillis = now) else it }
        return copy(intentions = closed + UsageIntent(sessionId = id, text = text.trim(), createdAtEpochMillis = now))
    }

    private fun transition(target: FocusSessionStatus, now: Long): FocusSession = when (target) {
        FocusSessionStatus.ACTIVE -> {
            require(status == FocusSessionStatus.PLANNED)
            copy(status = target, startedAtEpochMillis = now)
        }
        FocusSessionStatus.PAUSED -> {
            require(status == FocusSessionStatus.ACTIVE)
            copy(status = target, pauseStartedAtEpochMillis = now)
        }
        else -> error("Unsupported transition")
    }

    private fun finish(target: FocusSessionStatus, now: Long): FocusSession {
        require(status == FocusSessionStatus.PLANNED || status == FocusSessionStatus.ACTIVE || status == FocusSessionStatus.PAUSED)
        val extraPause = if (status == FocusSessionStatus.PAUSED) (now - (pauseStartedAtEpochMillis ?: now)).coerceAtLeast(0) else 0
        return copy(status = target, endedAtEpochMillis = now, accumulatedPauseMillis = accumulatedPauseMillis + extraPause, pauseStartedAtEpochMillis = null)
    }
}
