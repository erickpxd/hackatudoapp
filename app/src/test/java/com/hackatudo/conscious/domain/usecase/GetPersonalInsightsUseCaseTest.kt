package com.hackatudo.conscious.domain.usecase

import com.hackatudo.conscious.domain.model.SessionSummarySource
import com.hackatudo.conscious.domain.repository.SessionSummaryRepository
import com.hackatudo.conscious.domain.usecase.insights.GetPersonalInsightsUseCase
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetPersonalInsightsUseCaseTest {
    @Test
    fun `aggregates by day and immediately excludes deleted sessions`() = runTest {
        val first = completedSource(1 * DAY, 30_000)
        val second = completedSource(2 * DAY, 10_000)
        val repository = FakeSummaryRepository(listOf(first, second))
        val useCase = GetPersonalInsightsUseCase(repository)

        assertEquals(2, useCase().first().sessionCount)
        assertEquals(20_000, useCase().first().averageDurationMillis)

        repository.values.value = listOf(second)

        val afterDeletion = useCase().first()
        assertEquals(1, afterDeletion.sessionCount)
        assertEquals(10_000, afterDeletion.averageDurationMillis)
    }

    private class FakeSummaryRepository(initial: List<SessionSummarySource>) : SessionSummaryRepository {
        val values = MutableStateFlow(initial)
        override suspend fun getSource(sessionId: UUID): SessionSummarySource? = values.value.firstOrNull { it.sessionId == sessionId }
        override fun observeSources(): Flow<List<SessionSummarySource>> = values
    }

    private fun completedSource(endedAt: Long, duration: Long) = SessionSummarySource(
        sessionId = UUID.randomUUID(),
        title = "Sessão",
        plannedDurationMillis = duration,
        startedAtEpochMillis = endedAt - duration,
        endedAtEpochMillis = endedAt,
        accumulatedPauseMillis = 0,
        interventionCount = 0,
        stayFocusedCount = 0,
        openAnywayCount = 0,
        consciousIntentChangeCount = 0,
    )

    private companion object {
        const val DAY = 86_400_000L
    }
}
