package com.hackatudo.conscious.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import java.util.UUID
import kotlinx.coroutines.flow.Flow

data class SessionSummaryRow(
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

@Dao
interface SessionSummaryDao {
    @Query(SUMMARY_QUERY + " WHERE focus_sessions.id = :sessionId")
    suspend fun getSummary(sessionId: UUID): SessionSummaryRow?

    @Query(
        SUMMARY_QUERY +
            " WHERE focus_sessions.status IN ('COMPLETED', 'CANCELLED')" +
            " ORDER BY focus_sessions.endedAtEpochMillis DESC, focus_sessions.createdAtEpochMillis DESC",
    )
    fun observeHistory(): Flow<List<SessionSummaryRow>>

    companion object {
        const val SUMMARY_QUERY = """
            SELECT
                focus_sessions.id AS sessionId,
                focus_sessions.title AS title,
                focus_sessions.plannedDurationMillis AS plannedDurationMillis,
                focus_sessions.startedAtEpochMillis AS startedAtEpochMillis,
                focus_sessions.endedAtEpochMillis AS endedAtEpochMillis,
                focus_sessions.accumulatedPauseMillis AS accumulatedPauseMillis,
                (SELECT COUNT(*) FROM session_events
                    WHERE session_events.sessionId = focus_sessions.id
                    AND session_events.type = 'INTERVENTION_SHOWN') AS interventionCount,
                (SELECT COUNT(*) FROM session_events
                    WHERE session_events.sessionId = focus_sessions.id
                    AND session_events.type = 'INTERVENTION_DECIDED'
                    AND session_events.decision = 'STAY_FOCUSED') AS stayFocusedCount,
                (SELECT COUNT(*) FROM session_events
                    WHERE session_events.sessionId = focus_sessions.id
                    AND session_events.type = 'INTERVENTION_DECIDED'
                    AND session_events.decision = 'OPEN_ANYWAY') AS openAnywayCount,
                CASE WHEN (SELECT COUNT(*) FROM usage_intentions
                    WHERE usage_intentions.sessionId = focus_sessions.id) > 1
                    THEN (SELECT COUNT(*) - 1 FROM usage_intentions
                        WHERE usage_intentions.sessionId = focus_sessions.id)
                    ELSE 0 END AS consciousIntentChangeCount
            FROM focus_sessions
        """
    }
}
