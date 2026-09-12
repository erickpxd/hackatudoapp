package com.hackatudo.conscious.feature.session.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CreateSessionScreen(
    state: CreateSessionUiState,
    onIntentionChange: (String) -> Unit,
    onDurationChange: (Int) -> Unit,
    onToggleApp: (String) -> Unit,
    onApplyContext: (com.hackatudo.conscious.domain.model.FocusContext) -> Unit,
    onStart: () -> Unit,
    onSaveContext: (String) -> Unit = {},
) {
    var contextName by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { Text("Nova sessão") }
        if (state.contexts.isNotEmpty()) {
            item { Text("Contextos") }
            items(state.contexts, key = { it.id }) { context ->
                FilterChip(selected = false, onClick = { onApplyContext(context) }, label = { Text(context.name) })
            }
        }
        item {
            OutlinedTextField(
                value = state.intention,
                onValueChange = onIntentionChange,
                label = { Text("Qual é sua intenção?") },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(contextName, { contextName = it }, label = { Text("Nome do contexto") }, modifier = Modifier.fillMaxWidth(0.65f))
                Button(onClick = { onSaveContext(contextName); contextName = "" }, enabled = contextName.isNotBlank()) { Text("Salvar") }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(15, 30, 50).forEach { minutes ->
                    FilterChip(selected = state.durationMinutes == minutes, onClick = { onDurationChange(minutes) }, label = { Text("$minutes min") })
                }
            }
        }
        item { Text("Aplicativos relacionados") }
        items(state.apps, key = { it.packageName }) { app ->
            FilterChip(
                selected = app.packageName in state.selectedPackages,
                onClick = { onToggleApp(app.packageName) },
                label = { Text(app.displayName) },
            )
        }
        state.error?.let { message -> item { Text(message) } }
        item { Button(onClick = onStart, modifier = Modifier.fillMaxWidth()) { Text("Iniciar sessão") } }
    }
}
