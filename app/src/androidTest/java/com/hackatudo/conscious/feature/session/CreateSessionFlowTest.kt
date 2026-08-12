package com.hackatudo.conscious.feature.session

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.hackatudo.conscious.domain.model.InstalledApp
import com.hackatudo.conscious.feature.session.create.CreateSessionScreen
import com.hackatudo.conscious.feature.session.create.CreateSessionUiState
import org.junit.Rule
import org.junit.Test

class CreateSessionFlowTest {
    @get:Rule val rule = createComposeRule()
    @Test fun showsFirstSessionSetupStep() {
        rule.setContent { CreateSessionScreen(CreateSessionUiState(apps = listOf(InstalledApp("calculator", "Calculadora", "calculator", true, false))), {}, {}, {}, {}, {}) }
        rule.onNodeWithText("O que você quer fazer?", useUnmergedTree = true).assertIsDisplayed()
        rule.onNodeWithText("Estudar").assertIsDisplayed()
    }
}
