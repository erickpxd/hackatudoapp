package com.hackatudo.conscious.feature.session.suggestion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hackatudo.conscious.domain.model.PedagogicalSuggestion

@Composable
fun SuggestionScreen(item: PedagogicalSuggestion, onAccept: () -> Unit, onAdapt: () -> Unit, onIgnore: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Sugestão de ${item.sourceName}")
        Text(item.title)
        Text(item.suggestedIntention)
        Text("${item.durationMinutes} minutos. Você mantém o controle e pode editar tudo antes de começar.")
        Button(onClick = onAccept, modifier = Modifier.fillMaxWidth()) { Text("Aceitar e revisar") }
        OutlinedButton(onClick = onAdapt, modifier = Modifier.fillMaxWidth()) { Text("Adaptar sugestão") }
        OutlinedButton(onClick = onIgnore, modifier = Modifier.fillMaxWidth()) { Text("Ignorar") }
    }
}
