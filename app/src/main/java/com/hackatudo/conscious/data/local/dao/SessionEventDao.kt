package com.hackatudo.conscious.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hackatudo.conscious.data.local.entity.SessionEventEntity
import java.util.UUID
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionEventDao {
    @Query("SELECT * FROM session_events WHERE sessionId = :sessionId ORDER BY occurredAtEpochMillis, id")
    fun observeForSession(sessionId: UUID): Flow<List<SessionEventEntity>>

    @Query("SELECT * FROM session_events WHERE sessionId = :sessionId ORDER BY occurredAtEpochMillis, id")
    suspend fun getForSession(sessionId: UUID): List<SessionEventEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: SessionEventEntity)
}
