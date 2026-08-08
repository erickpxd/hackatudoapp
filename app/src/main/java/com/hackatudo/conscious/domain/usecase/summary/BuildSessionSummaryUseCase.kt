package com.hackatudo.conscious.domain.usecase.summary

import com.hackatudo.conscious.core.time.Clock
import com.hackatudo.conscious.domain.model.PersonalSummary
import com.hackatudo.conscious.domain.model.SessionSummarySource
import com.hackatudo.conscious.domain.repository.SessionSummaryRepository
import java.util.UUID
import javax.inject.Inject

class BuildSessionSummaryUseCase @Inject constructor(
    private val summaries: SessionSummaryRepository,
    private val clock: Clock,
) {
    suspend operator fun invoke(sessionId: UUID): PersonalSummary =
        requireNotNull(summaries.getSource(sessionId)) { "Sessão não encontrada" }.toSummary(clock.nowEpochMillis())
}

internal fun SessionSummarySource.toSummary(nowEpochMillis: Long): PersonalSummary {
    val start = startedAtEpochMillis ?: endedAtEpochMillis ?: nowEpochMillis
    val end = endedAtEpochMillis ?: nowEpochMillis
    val actual = (end - start - accumulatedPauseMillis).coerceAtLeast(0)
    return PersonalSummary(
        sessionId = sessionId,
        title = title,
        plannedDurationMillis = plannedDurationMillis,
        actualDurationMillis = actual,
        occurredAtEpochMillis = endedAtEpochMillis ?: start,
        interventionCount = interventionCount.coerceAtLeast(0),
        stayFocusedCount = stayFocusedCount.coerceAtLeast(0),
        openAnywayCount = openAnywayCount.coerceAtLeast(0),
        consciousIntentChangeCount = consciousIntentChangeCount.coerceAtLeast(0),
    )
}
