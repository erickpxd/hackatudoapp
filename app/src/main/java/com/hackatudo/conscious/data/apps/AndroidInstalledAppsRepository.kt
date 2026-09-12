package com.hackatudo.conscious.data.apps

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import com.hackatudo.conscious.domain.model.InstalledApp
import com.hackatudo.conscious.domain.repository.InstalledAppsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.text.Collator
import javax.inject.Inject

fun List<InstalledApp>.normalizedLaunchableApps(): List<InstalledApp> {
    val collator = Collator.getInstance()
    return filter(InstalledApp::launchable)
        .distinctBy(InstalledApp::packageName)
        .sortedWith { first, second -> collator.compare(first.displayName, second.displayName) }
}

class AndroidInstalledAppsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) : InstalledAppsRepository {
    private val packageManager: PackageManager get() = context.packageManager

    override fun observeLaunchableApps(): Flow<List<InstalledApp>> = flow {
        val launcherIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val apps = packageManager.queryIntentActivities(launcherIntent, PackageManager.MATCH_DEFAULT_ONLY)
            .mapNotNull { result ->
                val activityInfo = result.activityInfo ?: return@mapNotNull null
                val packageName = activityInfo.packageName ?: return@mapNotNull null
                if (packageName == context.packageName) return@mapNotNull null
                InstalledApp(
                    packageName = packageName,
                    displayName = result.loadLabel(packageManager)?.toString()?.ifBlank { packageName } ?: packageName,
                    iconKey = packageName,
                    launchable = packageManager.getLaunchIntentForPackage(packageName) != null,
                    systemApp = activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0,
                )
            }
        emit(apps.normalizedLaunchableApps())
    }.flowOn(Dispatchers.IO)

    fun loadIcon(packageName: String): Drawable? = runCatching {
        packageManager.getApplicationIcon(packageName)
    }.getOrNull()
}
