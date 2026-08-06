package com.hackatudo.conscious.feature.launcher

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.hackatudo.conscious.core.launcher.HomeRoleStatus
import com.hackatudo.conscious.domain.model.InstalledApp
import org.junit.Rule
import org.junit.Test

class HomeLauncherScreenTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun displaysAppsAndEmptyState() {
        composeRule.setContent {
            HomeLauncherScreen(
                state = LauncherUiState(
                    apps = listOf(InstalledApp("calculator", "Calculadora", "calculator", true, false)),
                    homeRoleStatus = HomeRoleStatus.SELECTED,
                ),
                onAppClick = {},
                onRequestHomeRole = {},
                onRetry = {},
            )
        }
        composeRule.onNodeWithText("Calculadora").assertIsDisplayed()
    }
}
