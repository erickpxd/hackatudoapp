package com.hackatudo.conscious.data.apps

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Process
import android.provider.Settings
import com.hackatudo.conscious.domain.repository.PersonalUsageStat
import com.hackatudo.conscious.domain.repository.PersonalUsageStatsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/** Optional local-only adapter. It is intentionally not bound into any P0 feature. */
class AndroidUsageStatsRepository @Inject constructor(@ApplicationContext private val context: Context) : PersonalUsageStatsRepository {
    override fun isAvailable() = context.getSystemService(UsageStatsManager::class.java) != null

    override fun hasPermission(): Boolean {
        val appOps = context.getSystemService(AppOpsManager::class.java) ?: return false
        return appOps.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName) == AppOpsManager.MODE_ALLOWED
    }

    override fun createPermissionSettingsIntent() = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)

    override suspend fun queryLocal(startEpochMillis: Long, endEpochMillis: Long): List<PersonalUsageStat> {
        if (!hasPermission()) return emptyList()
        val manager = context.getSystemService(UsageStatsManager::class.java) ?: return emptyList()
        return manager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startEpochMillis, endEpochMillis)
            .filter { it.totalTimeInForeground > 0 }
            .map { PersonalUsageStat(it.packageName, it.totalTimeInForeground) }
    }
}
