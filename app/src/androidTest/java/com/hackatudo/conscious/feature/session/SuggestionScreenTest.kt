package com.hackatudo.conscious.feature.session

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.hackatudo.conscious.domain.model.PedagogicalSuggestion
import com.hackatudo.conscious.feature.session.suggestion.SuggestionScreen
import org.junit.Rule
import org.junit.Test

class SuggestionScreenTest {
    @get:Rule val rule = createComposeRule()
    @Test fun showsSourceAndAllAutonomousChoices() {
        rule.setContent { SuggestionScreen(PedagogicalSuggestion(sourceName = "Professora Ana", title = "Matemática", suggestedIntention = "Revisar frações", durationMinutes = 30), {}, {}, {}) }
        rule.onNodeWithText("Professora Ana", substring = true).assertIsDisplayed()
        rule.onNodeWithText("Aceitar e revisar").assertIsDisplayed()
        rule.onNodeWithText("Adaptar sugestão").assertIsDisplayed()
        rule.onNodeWithText("Ignorar").assertIsDisplayed()
    }
}
