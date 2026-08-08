package com.hackatudo.conscious.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.hackatudo.conscious.domain.model.InterventionDecision
import com.hackatudo.conscious.domain.model.ReflectionReason
import com.hackatudo.conscious.domain.model.SessionEventType
import java.util.UUID

@Entity(
    tableName = "session_events",
    foreignKeys = [
        ForeignKey(
            entity = FocusSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("sessionId"), Index(value = ["sessionId", "occurredAtEpochMillis"])],
)
data class SessionEventEntity(
    @PrimaryKey val id: UUID,
    val sessionId: UUID,
    val type: SessionEventType,
    val occurredAtEpochMillis: Long,
    val targetPackage: String?,
    val decision: InterventionDecision?,
    val reflectionReason: ReflectionReason?,
    val intentId: UUID?,
)
