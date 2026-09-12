package com.hackatudo.conscious.domain.repository

import com.hackatudo.conscious.domain.model.InstalledApp
import kotlinx.coroutines.flow.Flow

interface InstalledAppsRepository {
    fun observeLaunchableApps(): Flow<List<InstalledApp>>
}
