package com.hackatudo.conscious.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.hackatudo.conscious.domain.model.ContentSource
import com.hackatudo.conscious.domain.model.FocusSessionStatus
import java.util.UUID

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey val id: UUID,
    val title: String,
    val plannedDurationMillis: Long,
    val createdAtEpochMillis: Long,
    val status: FocusSessionStatus,
    val startedAtEpochMillis: Long?,
    val endedAtEpochMillis: Long?,
    val accumulatedPauseMillis: Long,
    val pauseStartedAtEpochMillis: Long?,
    val groupId: UUID?,
)

@Entity(
    tableName = "usage_intentions",
    foreignKeys = [ForeignKey(entity = FocusSessionEntity::class, parentColumns = ["id"], childColumns = ["sessionId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("sessionId")],
)
data class UsageIntentEntity(
    @PrimaryKey val id: UUID,
    val sessionId: UUID,
    val text: String,
    val source: ContentSource,
    val createdAtEpochMillis: Long,
    val activeUntilEpochMillis: Long?,
)

@Entity(
    tableName = "session_apps",
    primaryKeys = ["sessionId", "packageName"],
    foreignKeys = [ForeignKey(entity = FocusSessionEntity::class, parentColumns = ["id"], childColumns = ["sessionId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("sessionId")],
)
data class SessionAppEntity(val sessionId: UUID, val packageName: String)

@Entity(tableName = "focus_contexts")
data class FocusContextEntity(
    @PrimaryKey val id: UUID,
    val name: String,
    val suggestedIntention: String,
    val relatedPackageNames: List<String>,
    val source: ContentSource,
)
