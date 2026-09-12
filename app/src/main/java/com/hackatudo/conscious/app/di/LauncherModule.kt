package com.hackatudo.conscious.app.di

import com.hackatudo.conscious.core.launcher.AndroidAppLauncher
import com.hackatudo.conscious.core.launcher.AndroidHomeRoleManager
import com.hackatudo.conscious.core.launcher.AppLauncher
import com.hackatudo.conscious.core.launcher.HomeRoleManager
import com.hackatudo.conscious.data.apps.AndroidInstalledAppsRepository
import com.hackatudo.conscious.domain.repository.InstalledAppsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LauncherModule {
    @Binds @Singleton abstract fun bindInstalledApps(implementation: AndroidInstalledAppsRepository): InstalledAppsRepository
    @Binds @Singleton abstract fun bindAppLauncher(implementation: AndroidAppLauncher): AppLauncher
    @Binds @Singleton abstract fun bindHomeRoleManager(implementation: AndroidHomeRoleManager): HomeRoleManager
}
