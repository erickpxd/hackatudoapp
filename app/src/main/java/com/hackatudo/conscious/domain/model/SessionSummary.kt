package com.hackatudo.conscious.domain.model

import java.util.UUID

data class SessionSummarySource(
    val sessionId: UUID,
    val title: String,
    val plannedDurationMillis: Long,
    val startedAtEpochMillis: Long?,
    val endedAtEpochMillis: Long?,
    val accumulatedPauseMillis: Long,
    val interventionCount: Int,
    val stayFocusedCount: Int,
    val openAnywayCount: Int,
    val consciousIntentChangeCount: Int,
)

data class PersonalSummary(
    val sessionId: UUID,
    val title: String,
    val plannedDurationMillis: Long,
    val actualDurationMillis: Long,
    val occurredAtEpochMillis: Long,
    val interventionCount: Int,
    val stayFocusedCount: Int,
    val openAnywayCount: Int,
    val consciousIntentChangeCount: Int,
)

data class DailyPersonalInsight(
    val epochDay: Long,
    val sessionCount: Int,
    val totalDurationMillis: Long,
)

data class PersonalInsights(
    val sessionCount: Int = 0,
    val totalDurationMillis: Long = 0,
    val averageDurationMillis: Long = 0,
    val activeDayCount: Int = 0,
    val currentConsistencyDays: Int = 0,
    val daily: List<DailyPersonalInsight> = emptyList(),
)
