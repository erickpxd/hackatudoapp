package com.hackatudo.conscious.domain.usecase.insights

import com.hackatudo.conscious.domain.model.DailyPersonalInsight
import com.hackatudo.conscious.domain.model.PersonalInsights
import com.hackatudo.conscious.domain.repository.SessionSummaryRepository
import com.hackatudo.conscious.domain.usecase.summary.toSummary
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetPersonalInsightsUseCase @Inject constructor(
    private val summaries: SessionSummaryRepository,
) {
    operator fun invoke(): Flow<PersonalInsights> = summaries.observeSources().map { sources ->
        val values = sources.map { it.toSummary(it.endedAtEpochMillis ?: it.startedAtEpochMillis ?: 0) }
        val daily = values.groupBy { it.occurredAtEpochMillis / DAY_MILLIS }
            .map { (day, sessions) ->
                DailyPersonalInsight(day, sessions.size, sessions.sumOf { it.actualDurationMillis })
            }
            .sortedBy { it.epochDay }
        val total = values.sumOf { it.actualDurationMillis }
        PersonalInsights(
            sessionCount = values.size,
            totalDurationMillis = total,
            averageDurationMillis = if (values.isEmpty()) 0 else total / values.size,
            activeDayCount = daily.size,
            currentConsistencyDays = consistency(daily.map { it.epochDay }),
            daily = daily,
        )
    }

    private fun consistency(days: List<Long>): Int {
        if (days.isEmpty()) return 0
        var result = 1
        for (index in days.lastIndex downTo 1) {
            if (days[index] - days[index - 1] != 1L) break
            result++
        }
        return result
    }

    private companion object {
        const val DAY_MILLIS = 86_400_000L
    }
}
