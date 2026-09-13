package com.hackatudo.conscious.domain.usecase

import com.hackatudo.conscious.core.time.Clock
import com.hackatudo.conscious.domain.model.SessionSummarySource
import com.hackatudo.conscious.domain.repository.SessionSummaryRepository
import com.hackatudo.conscious.domain.usecase.summary.BuildSessionSummaryUseCase
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class BuildSessionSummaryUseCaseTest {
    private class FixedClock(private val now: Long) : Clock {
        override fun nowEpochMillis(): Long = now
    }

    @Test
    fun `calculates active duration without pauses and event counts`() = runTest {
        val source = source(
            startedAt = 1_000,
            endedAt = 11_000,
            accumulatedPause = 2_000,
            interventionCount = 3,
            stayFocusedCount = 2,
            openAnywayCount = 1,
            intentChangeCount = 2,
        )

        val summary = BuildSessionSummaryUseCase(FakeSummaryRepository(source), FixedClock(50_000))(source.sessionId)

        assertEquals(8_000, summary.actualDurationMillis)
        assertEquals(3, summary.interventionCount)
        assertEquals(2, summary.stayFocusedCount)
        assertEquals(1, summary.openAnywayCount)
        assertEquals(2, summary.consciousIntentChangeCount)
    }

    @Test
    fun `clock changes never produce negative duration`() = runTest {
        val source = source(startedAt = 10_000, endedAt = null, accumulatedPause = 3_000)

        val summary = BuildSessionSummaryUseCase(FakeSummaryRepository(source), FixedClock(5_000))(source.sessionId)

        assertEquals(0, summary.actualDurationMillis)
    }

    private class FakeSummaryRepository(private val value: SessionSummarySource) : SessionSummaryRepository {
        override suspend fun getSource(sessionId: UUID): SessionSummarySource? = value.takeIf { it.sessionId == sessionId }
        override fun observeSources(): Flow<List<SessionSummarySource>> = flowOf(listOf(value))
    }

    private fun source(
        startedAt: Long,
        endedAt: Long?,
        accumulatedPause: Long,
        interventionCount: Int = 0,
        stayFocusedCount: Int = 0,
        openAnywayCount: Int = 0,
        intentChangeCount: Int = 0,
    ) = SessionSummarySource(
        sessionId = UUID.randomUUID(),
        title = "Matemática",
        plannedDurationMillis = 30_000,
        startedAtEpochMillis = startedAt,
        endedAtEpochMillis = endedAt,
        accumulatedPauseMillis = accumulatedPause,
        interventionCount = interventionCount,
        stayFocusedCount = stayFocusedCount,
        openAnywayCount = openAnywayCount,
        consciousIntentChangeCount = intentChangeCount,
    )
}
