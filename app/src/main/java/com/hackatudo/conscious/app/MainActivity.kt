package com.hackatudo.conscious.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hackatudo.conscious.core.datastore.PrivacyPreferencesDataStore
import com.hackatudo.conscious.core.designsystem.theme.ConsciousUseTheme
import com.hackatudo.conscious.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import com.hackatudo.conscious.domain.model.FocusSessionStatus
import com.hackatudo.conscious.domain.model.SessionEventType
import com.hackatudo.conscious.domain.repository.SessionEventRepository
import com.hackatudo.conscious.domain.usecase.intervention.RecordInterventionShownUseCase
import com.hackatudo.conscious.domain.usecase.session.GetCurrentSessionUseCase
import com.hackatudo.conscious.domain.usecase.session.PauseFocusSessionUseCase

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var launcherActions: LauncherActions
    @Inject lateinit var preferences: PrivacyPreferencesDataStore
    @Inject lateinit var currentSession: GetCurrentSessionUseCase
    @Inject lateinit var pauseSession: PauseFocusSessionUseCase
    @Inject lateinit var sessionEvents: SessionEventRepository
    @Inject lateinit var recordInterventionShown: RecordInterventionShownUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val initialProfile = runBlocking { preferences.preferences.first() }
        setContent {
            val profile by preferences.preferences.collectAsStateWithLifecycle(initialValue = initialProfile)
            var showingBrandSplash by remember { mutableStateOf(true) }
            LaunchedEffect(Unit) {
                delay(850)
                showingBrandSplash = false
            }
            ConsciousUseTheme(darkTheme = true, themeColor = profile.themeColor) {
                if (showingBrandSplash) {
                    Box(
                        Modifier.fillMaxSize().background(androidx.compose.material3.MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.gedu_logo_splash),
                            contentDescription = "GEDU",
                            modifier = Modifier.width(310.dp).height(130.dp),
                            contentScale = ContentScale.FillWidth,
                        )
                    }
                } else {
                    AppNavHost(
                        actions = launcherActions,
                        onboardingCompleted = profile.onboardingCompleted,
                    )
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (isChangingConfigurations || isFinishing) return
        lifecycleScope.launch {
            val session = currentSession().first() ?: return@launch
            if (session.status != FocusSessionStatus.ACTIVE) return@launch

            val latestEvent = sessionEvents.getForSession(session.id).maxByOrNull { it.occurredAtEpochMillis }
            val negotiationJustShown = latestEvent?.type == SessionEventType.INTERVENTION_SHOWN &&
                System.currentTimeMillis() - latestEvent.occurredAtEpochMillis < 5_000

            pauseSession(session.id)
            if (!negotiationJustShown) {
                recordInterventionShown(session.id, "gedu:app-background")
            }
        }
    }
}
