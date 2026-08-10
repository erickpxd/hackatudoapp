package com.hackatudo.conscious.feature.session

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.hackatudo.conscious.domain.model.PedagogicalSuggestion
import com.hackatudo.conscious.feature.session.suggestion.SuggestionScreen
import org.junit.Rule
import org.junit.Test

class SuggestionScreenTest {
    @get:Rule val rule = createComposeRule()
    @Test fun showsSourceAndAllAutonomousChoices() {
        var accepted = false
        var adapted = false
        var ignored = false
        rule.setContent {
            SuggestionScreen(
                PedagogicalSuggestion(sourceName = "Professora Ana", title = "Matemática", suggestedIntention = "Revisar frações", durationMinutes = 30),
                { accepted = true },
                { adapted = true },
                { ignored = true },
            )
        }
        rule.onNodeWithText("Professora Ana", substring = true).assertIsDisplayed()
        rule.onNodeWithText("Aceitar e revisar").assertIsDisplayed().performClick()
        rule.onNodeWithText("Adaptar sugestão").assertIsDisplayed().performClick()
        rule.onNodeWithText("Ignorar").assertIsDisplayed().performClick()
        assert(accepted && adapted && ignored)
    }
}
