package com.hackatudo.conscious.data.repository

import com.hackatudo.conscious.core.database.AppDatabase
import com.hackatudo.conscious.data.local.dao.SessionSummaryRow
import com.hackatudo.conscious.domain.model.SessionSummarySource
import com.hackatudo.conscious.domain.repository.SessionSummaryRepository
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomSessionSummaryRepository @Inject constructor(
    database: AppDatabase,
) : SessionSummaryRepository {
    private val dao = database.sessionSummaryDao()

    override suspend fun getSource(sessionId: UUID): SessionSummarySource? =
        dao.getSummary(sessionId)?.toDomain()

    override fun observeSources(): Flow<List<SessionSummarySource>> =
        dao.observeHistory().map { rows -> rows.map(SessionSummaryRow::toDomain) }
}

private fun SessionSummaryRow.toDomain() = SessionSummarySource(
    sessionId = sessionId,
    title = title,
    plannedDurationMillis = plannedDurationMillis,
    startedAtEpochMillis = startedAtEpochMillis,
    endedAtEpochMillis = endedAtEpochMillis,
    accumulatedPauseMillis = accumulatedPauseMillis,
    interventionCount = interventionCount,
    stayFocusedCount = stayFocusedCount,
    openAnywayCount = openAnywayCount,
    consciousIntentChangeCount = consciousIntentChangeCount,
)
