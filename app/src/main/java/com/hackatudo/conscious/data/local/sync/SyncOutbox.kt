package com.hackatudo.conscious.data.local.sync

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import java.util.UUID
import kotlinx.coroutines.flow.Flow

enum class SyncStatus { PENDING, SYNCED, FAILED }

@Entity(tableName = "sync_outbox")
data class SyncOutboxEntity(
    @PrimaryKey val id: UUID,
    val groupId: UUID,
    val durationMinutes: Int,
    val completed: Boolean,
    val occurredAtEpochMillis: Long,
    val status: SyncStatus = SyncStatus.PENDING,
    val attemptCount: Int = 0,
    val lastErrorCode: String? = null,
)

@Dao
interface SyncOutboxDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE) suspend fun enqueue(entry: SyncOutboxEntity): Long
    @Query("SELECT * FROM sync_outbox WHERE status IN ('PENDING', 'FAILED') ORDER BY occurredAtEpochMillis LIMIT :limit")
    suspend fun pending(limit: Int = 20): List<SyncOutboxEntity>
    @Query("SELECT * FROM sync_outbox ORDER BY occurredAtEpochMillis DESC") fun observeAll(): Flow<List<SyncOutboxEntity>>
    @Query("UPDATE sync_outbox SET status = :status, attemptCount = attemptCount + 1, lastErrorCode = :errorCode WHERE id = :id")
    suspend fun updateStatus(id: UUID, status: SyncStatus, errorCode: String? = null)
}
