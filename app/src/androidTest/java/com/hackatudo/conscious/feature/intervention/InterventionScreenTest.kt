package com.hackatudo.conscious.feature.intervention

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class InterventionScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun showsContextAndTwoAutonomousNeutralActions() {
        composeRule.setContent {
            InterventionScreen(
                state = InterventionUiState(
                    intention = "Estudar matemática",
                    appName = "Mensagens",
                    remainingMillis = 12 * 60_000,
                ),
                onReasonSelected = {},
                onStayFocused = {},
                onOpenAnyway = {},
            )
        }

        composeRule.onNodeWithText("Estudar matemática", substring = true).assertIsDisplayed()
        composeRule.onNodeWithText("Mensagens", substring = true).assertIsDisplayed()
        composeRule.onNodeWithText("12 min", substring = true).assertIsDisplayed()
        composeRule.onNodeWithText("Continuar focado").assertIsDisplayed()
        composeRule.onNodeWithText("Abrir mesmo assim").assertIsDisplayed()
    }
}
