package com.hackatudo.conscious.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.hackatudo.conscious.data.local.entity.FocusSessionEntity
import com.hackatudo.conscious.data.local.entity.SessionAppEntity
import com.hackatudo.conscious.data.local.entity.UsageIntentEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

data class FocusSessionRecord(
    @androidx.room.Embedded val session: FocusSessionEntity,
    @androidx.room.Relation(parentColumn = "id", entityColumn = "sessionId") val intentions: List<UsageIntentEntity>,
    @androidx.room.Relation(parentColumn = "id", entityColumn = "sessionId") val apps: List<SessionAppEntity>,
)

@Dao
interface FocusSessionDao {
    @Transaction
    @Query("SELECT * FROM focus_sessions WHERE status IN ('ACTIVE','PAUSED') ORDER BY createdAtEpochMillis DESC LIMIT 1")
    fun observeCurrent(): Flow<FocusSessionRecord?>

    @Transaction
    @Query("SELECT * FROM focus_sessions WHERE id = :id")
    suspend fun get(id: UUID): FocusSessionRecord?

    @Upsert suspend fun upsertSession(entity: FocusSessionEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertIntent(entity: UsageIntentEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertApps(entities: List<SessionAppEntity>)
    @Query("DELETE FROM session_apps WHERE sessionId = :sessionId") suspend fun deleteApps(sessionId: UUID)
    @Query("UPDATE usage_intentions SET activeUntilEpochMillis = :endedAt WHERE sessionId = :sessionId AND activeUntilEpochMillis IS NULL") suspend fun closeCurrentIntent(sessionId: UUID, endedAt: Long)
    @Query("DELETE FROM focus_sessions WHERE id = :sessionId") suspend fun deleteSession(sessionId: UUID)
    @Query("DELETE FROM focus_sessions") suspend fun deleteAllSessions()

    @Transaction
    suspend fun replace(record: FocusSessionRecord) {
        upsertSession(record.session)
        record.intentions.forEach { upsertIntent(it) }
        deleteApps(record.session.id)
        upsertApps(record.apps)
    }
}
