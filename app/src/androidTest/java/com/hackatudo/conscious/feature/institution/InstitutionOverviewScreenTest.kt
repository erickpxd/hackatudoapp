package com.hackatudo.conscious.feature.institution

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.hackatudo.conscious.data.demo.InstitutionalAggregateFixtures
import org.junit.Rule
import org.junit.Test

class InstitutionOverviewScreenTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun showsAggregatesWithoutIndividualNavigationControls() {
        rule.setContent { InstitutionOverviewScreen(InstitutionalAggregateFixtures.mathematics) }

        rule.onNodeWithText("Visão geral da turma").assertIsDisplayed()
        rule.onNodeWithText("Sessões: 42").assertIsDisplayed()
        rule.onNodeWithText("Duração média: 31 min").assertIsDisplayed()
        rule.onNodeWithText("Conclusão: 86%").assertIsDisplayed()
        rule.onNodeWithText("Intervenções: 9").assertIsDisplayed()
        rule.onNodeWithText("Tendência: Mais sessões concluídas nas últimas duas semanas").assertIsDisplayed()
        rule.onNodeWithText("Abrir estudante").assertDoesNotExist()
        rule.onNodeWithText("Filtrar estudante").assertDoesNotExist()
    }
}
