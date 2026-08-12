package com.hackatudo.conscious.feature.summary

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hackatudo.conscious.core.designsystem.component.ErrorState

private val SummaryBg = Color(0xFF111111)
private val SummaryCard = Color(0xFF202020)
private val SummaryMuted = Color(0xFF9A9896)

@Composable
fun SessionSummaryScreen(
    state: SessionSummaryUiState,
    onRetry: () -> Unit,
    onDone: () -> Unit,
    onDeleteSession: () -> Unit,
    onDeleteAllHistory: () -> Unit,
) {
    var confirmation by remember { mutableStateOf<DeleteConfirmation?>(null) }
    Box(Modifier.fillMaxSize().background(SummaryBg)) {
        when {
            state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
            state.summary == null -> ErrorState(state.errorMessage ?: "Resumo indisponível.", onRetry)
            else -> {
                val summary = state.summary
                val accent = MaterialTheme.colorScheme.primary
                Column(
                    Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Text("SESSÃO CONCLUÍDA", color = accent, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.4.sp)
                    Text("Bom trabalho!", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.semantics { heading() })
                    Text("Aqui está o resumo do seu tempo de foco.", color = SummaryMuted, fontSize = 13.sp)

                    val titleShape = RoundedCornerShape(20.dp)
                    Column(
                        Modifier.fillMaxWidth().background(SummaryCard, titleShape)
                            .border(1.dp, Color.White.copy(alpha = .08f), titleShape).padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text("ATIVIDADE", color = SummaryMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(summary.title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }

                    val metricsShape = RoundedCornerShape(20.dp)
                    Column(
                        Modifier.fillMaxWidth().background(SummaryCard, metricsShape)
                            .border(1.dp, Color.White.copy(alpha = .08f), metricsShape).padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        SummaryMetric("Duração planejada", formatDuration(summary.plannedDurationMillis))
                        SummaryMetric("Duração realizada", formatDuration(summary.actualDurationMillis), true)
                        HorizontalDivider(color = Color.White.copy(alpha = .08f))
                        SummaryMetric("Pausas para refletir", summary.interventionCount.toString())
                        SummaryMetric("Escolhas de continuar focado", summary.stayFocusedCount.toString())
                        SummaryMetric("Aberturas conscientes", summary.openAnywayCount.toString())
                        SummaryMetric("Mudanças de intenção", summary.consciousIntentChangeCount.toString())
                    }

                    Surface(color = accent.copy(alpha = .10f), shape = RoundedCornerShape(16.dp)) {
                        Text(
                            "Este resumo é privado. Suas escolhas não são classificadas como sucesso ou fracasso.",
                            Modifier.padding(16.dp), color = Color.White.copy(alpha = .82f), fontSize = 12.sp, lineHeight = 17.sp,
                        )
                    }
                    state.errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }

                    Button(onClick = onDone, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp)) {
                        Text("Voltar ao início", fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { confirmation = DeleteConfirmation.SESSION },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    ) { Text("Excluir esta sessão") }
                    TextButton(onClick = { confirmation = DeleteConfirmation.ALL }, modifier = Modifier.fillMaxWidth()) {
                        Text("Excluir todo o histórico", color = SummaryMuted)
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }

    confirmation?.let { requested ->
        AlertDialog(
            onDismissRequest = { confirmation = null },
            containerColor = SummaryCard,
            titleContentColor = Color.White,
            textContentColor = SummaryMuted,
            title = { Text(if (requested == DeleteConfirmation.ALL) "Excluir todo o histórico?" else "Excluir esta sessão?") },
            text = { Text("Sessões e dados pessoais derivados serão removidos. Onboarding e grupos serão mantidos.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmation = null
                    if (requested == DeleteConfirmation.ALL) onDeleteAllHistory() else onDeleteSession()
                }) { Text("Confirmar exclusão", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { confirmation = null }) { Text("Manter histórico") } },
        )
    }
}

@Composable
private fun SummaryMetric(label: String, value: String, highlighted: Boolean = false) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = SummaryMuted, fontSize = 12.sp)
        Text(value, color = if (highlighted) MaterialTheme.colorScheme.primary else Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

private fun formatDuration(millis: Long): String {
    val seconds = millis.coerceAtLeast(0) / 1_000
    return if (seconds < 60) "$seconds s" else "${seconds / 60} min"
}

private enum class DeleteConfirmation { SESSION, ALL }
