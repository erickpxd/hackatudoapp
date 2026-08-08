package com.hackatudo.conscious.feature.intervention

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.hackatudo.conscious.domain.model.ReflectionReason

@Composable
fun InterventionScreen(
    state: InterventionUiState,
    onReasonSelected: (ReflectionReason?) -> Unit,
    onStayFocused: () -> Unit,
    onOpenAnyway: () -> Unit,
    onChangeIntention: () -> Unit = {},
    onEndSession: () -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Uma pausa para escolher",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.semantics { heading() },
        )
        Text("Sua intenção atual é ${state.intention}.")
        Text("Você escolheu abrir ${state.appName}, que não está relacionado a esta sessão.")
        Text("Tempo restante: ${state.remainingMillis.coerceAtLeast(0) / 60_000} min")
        Text("Se quiser, registre um motivo. Essa resposta é opcional e fica somente neste aparelho.")

        ReflectionReason.entries.forEach { reason ->
            FilterChip(
                selected = state.selectedReason == reason,
                onClick = { onReasonSelected(reason.takeUnless { state.selectedReason == it }) },
                label = { Text(reason.label) },
            )
        }

        state.errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Button(
            onClick = onStayFocused,
            enabled = !state.isSubmitting,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Continuar focado")
        }
        OutlinedButton(
            onClick = onOpenAnyway,
            enabled = !state.isSubmitting,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Abrir mesmo assim")
        }
        OutlinedButton(onClick = onChangeIntention, modifier = Modifier.fillMaxWidth()) {
            Text("Mudar intenção")
        }
        OutlinedButton(onClick = onEndSession, modifier = Modifier.fillMaxWidth()) {
            Text("Encerrar sessão")
        }
        InterventionLimitationsText(Modifier.fillMaxWidth().wrapContentHeight())
    }
}

private val ReflectionReason.label: String
    get() = when (this) {
        ReflectionReason.NEEDED_FOR_ACTIVITY -> "Preciso para a atividade"
        ReflectionReason.CONTACT_SOMEONE -> "Quero falar com alguém"
        ReflectionReason.TAKE_BREAK -> "Quero fazer uma pausa"
        ReflectionReason.OPENED_BY_HABIT -> "Abri por hábito"
        ReflectionReason.INTENT_CHANGED -> "Minha intenção mudou"
        ReflectionReason.OTHER -> "Outro motivo"
    }
