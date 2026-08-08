package com.hackatudo.conscious.feature.summary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.hackatudo.conscious.core.designsystem.component.ErrorState

@Composable
fun SessionSummaryScreen(
    state: SessionSummaryUiState,
    onRetry: () -> Unit,
    onDone: () -> Unit,
    onDeleteSession: () -> Unit,
    onDeleteAllHistory: () -> Unit,
) {
    var confirmation by remember { mutableStateOf<DeleteConfirmation?>(null) }
    when {
        state.isLoading -> CircularProgressIndicator()
        state.summary == null -> ErrorState(state.errorMessage ?: "Resumo indisponível.", onRetry)
        else -> Column(
            Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            val summary = state.summary
            Text(
                "Resumo da sessão",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.semantics { heading() },
            )
            Text(summary.title)
            Text("Duração planejada: ${summary.plannedDurationMillis / 60_000} min")
            Text("Duração realizada: ${summary.actualDurationMillis / 60_000} min")
            Text("Pausas para refletir: ${summary.interventionCount}")
            Text("Escolhas de continuar focado: ${summary.stayFocusedCount}")
            Text("Aberturas conscientes: ${summary.openAnywayCount}")
            Text("Mudanças conscientes de intenção: ${summary.consciousIntentChangeCount}")
            Text("Este resumo é privado e não classifica suas escolhas como sucesso ou fracasso.")
            state.errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Button(onClick = onDone, modifier = Modifier.fillMaxWidth()) { Text("Voltar ao início") }
            OutlinedButton(
                onClick = { confirmation = DeleteConfirmation.SESSION },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Excluir esta sessão") }
            OutlinedButton(
                onClick = { confirmation = DeleteConfirmation.ALL },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Excluir todo o histórico") }
        }
    }

    confirmation?.let { requested ->
        AlertDialog(
            onDismissRequest = { confirmation = null },
            title = { Text(if (requested == DeleteConfirmation.ALL) "Excluir todo o histórico?" else "Excluir esta sessão?") },
            text = {
                Text("Sessões e dados pessoais derivados serão removidos. Onboarding e grupos não serão apagados.")
            },
            confirmButton = {
                TextButton(onClick = {
                    confirmation = null
                    if (requested == DeleteConfirmation.ALL) onDeleteAllHistory() else onDeleteSession()
                }) { Text("Confirmar exclusão") }
            },
            dismissButton = {
                TextButton(onClick = { confirmation = null }) { Text("Manter histórico") }
            },
        )
    }
}

private enum class DeleteConfirmation { SESSION, ALL }
