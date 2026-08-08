package com.hackatudo.conscious.domain.repository

import android.content.Intent

data class PersonalUsageStat(val packageName: String, val foregroundMillis: Long)

interface PersonalUsageStatsRepository {
    fun isAvailable(): Boolean
    fun hasPermission(): Boolean
    fun createPermissionSettingsIntent(): Intent
    suspend fun queryLocal(startEpochMillis: Long, endEpochMillis: Long): List<PersonalUsageStat>
}
