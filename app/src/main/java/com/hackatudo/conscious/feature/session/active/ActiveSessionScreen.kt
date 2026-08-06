package com.hackatudo.conscious.feature.session.active

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hackatudo.conscious.domain.model.FocusSessionStatus

@Composable
fun ActiveSessionScreen(state: ActiveSessionUiState, onPause: () -> Unit, onResume: () -> Unit, onComplete: () -> Unit, onCancel: () -> Unit, onChangeIntention: (String) -> Unit) {
    val session = state.session ?: return
    var newIntention by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Intenção atual: ${session.currentIntention?.text ?: session.title}")
        Text("Estado: ${session.status.name.lowercase()}")
        Text("Tempo restante: ${(state.remainingMillis.coerceAtLeast(0) / 60_000)} min")
        if (session.status == FocusSessionStatus.ACTIVE) Button(onClick = onPause) { Text("Pausar") }
        if (session.status == FocusSessionStatus.PAUSED) Button(onClick = onResume) { Text("Retomar") }
        OutlinedTextField(newIntention, { newIntention = it }, label = { Text("Nova intenção") })
        Button(onClick = { onChangeIntention(newIntention); newIntention = "" }, enabled = newIntention.isNotBlank()) { Text("Atualizar intenção") }
        Button(onClick = onComplete) { Text("Concluir sessão") }
        Button(onClick = onCancel) { Text("Encerrar sessão") }
    }
}
