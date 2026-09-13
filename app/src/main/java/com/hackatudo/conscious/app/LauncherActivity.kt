package com.hackatudo.conscious.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hackatudo.conscious.core.datastore.PrivacyPreferencesDataStore
import com.hackatudo.conscious.core.designsystem.theme.ConsciousUseTheme
import com.hackatudo.conscious.feature.launcher.LauncherViewModel
import com.hackatudo.conscious.feature.launcher.SystemLauncherScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class LauncherActivity : ComponentActivity() {
    @Inject lateinit var launcherActions: LauncherActions
    @Inject lateinit var preferences: PrivacyPreferencesDataStore
    private val viewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val initialProfile = runBlocking { preferences.preferences.first() }
        setContent {
            val profile by preferences.preferences.collectAsStateWithLifecycle(initialValue = initialProfile)
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            LaunchedEffect(viewModel) {
                viewModel.effects.collect { effect ->
                    when (effect) {
                        is com.hackatudo.conscious.feature.launcher.LauncherEffect.OpenApp -> launcherActions.appLauncher.launch(effect.packageName)
                        is com.hackatudo.conscious.feature.launcher.LauncherEffect.ShowIntervention -> {
                            launcherActions.navigationRequests.requestIntervention(effect.packageName)
                            packageManager.getLaunchIntentForPackage(packageName)?.let { intent ->
                                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
                            }
                        }
                    }
                }
            }
            ConsciousUseTheme(darkTheme = true, themeColor = profile.themeColor) {
                SystemLauncherScreen(
                    state = state,
                    onAppClick = viewModel::requestAppLaunch,
                    onOpenGedu = {
                        packageManager.getLaunchIntentForPackage(packageName)?.let { intent ->
                            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
                        }
                    },
                    onRetry = viewModel::retry,
                )
            }
        }
    }
}
