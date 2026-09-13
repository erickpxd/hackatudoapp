package com.hackatudo.conscious

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.hackatudo.conscious.feature.intervention.InterventionScreen
import com.hackatudo.conscious.feature.intervention.InterventionUiState
import com.hackatudo.conscious.feature.onboarding.OnboardingScreen
import org.junit.Rule
import org.junit.Test

class AccessibilitySmokeTest {
    @get:Rule val rule = createComposeRule()

    @Test fun essentialChoicesHaveTextAndActionsIndependentOfColor() {
        rule.setContent { OnboardingScreen { _, _, _ -> } }
        rule.onNodeWithText("Começar").assertIsDisplayed().assertHasClickAction()

        rule.setContent {
            InterventionScreen(
                state = InterventionUiState(intention = "Estudar", appName = "Mensagens", remainingMillis = 600_000),
                onReasonSelected = {}, onStayFocused = {}, onOpenAnyway = {},
            )
        }
        rule.onNodeWithText("Continuar focado").assertIsDisplayed().assertHasClickAction()
        rule.onNodeWithText("Abrir mesmo assim").assertIsDisplayed().assertHasClickAction()
    }
}
