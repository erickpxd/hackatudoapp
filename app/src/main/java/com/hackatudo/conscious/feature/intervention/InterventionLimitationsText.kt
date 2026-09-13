package com.hackatudo.conscious.feature.intervention

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

@Composable
fun InterventionLimitationsText(modifier: Modifier = Modifier) {
    val explanation = "Esta pausa aparece somente quando você abre um app por este launcher. " +
        "Notificações, links e aberturas iniciadas por outros apps não são interceptados."
    Text(
        text = explanation,
        modifier = modifier.semantics { contentDescription = explanation },
    )
}
