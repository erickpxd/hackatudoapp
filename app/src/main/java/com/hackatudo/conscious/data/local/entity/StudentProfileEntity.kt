package com.hackatudo.conscious.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "student_profiles")
data class StudentProfileEntity(
    @PrimaryKey val id: UUID,
    val displayName: String?,
    val createdAtEpochMillis: Long,
)
