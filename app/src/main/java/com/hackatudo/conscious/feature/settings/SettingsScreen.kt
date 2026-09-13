package com.hackatudo.conscious.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(state: SettingsUiState, onDeleteHistory: () -> Unit, onUsageAccess: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Privacidade e dados")
        Text("Ficam neste aparelho: intenções, aplicativos relacionados, motivos, decisões, histórico pessoal e dados opcionais de uso.")
        Text("Podem ser compartilhados após login: identificador da contribuição, grupo, duração, conclusão e horário.")
        Text("O painel institucional recebe somente totais da turma. Ele não recebe histórico, aplicativos ou respostas individuais.")
        Button(onClick = onUsageAccess, modifier = Modifier.fillMaxWidth()) { Text("Indicador pessoal opcional") }
        Button(onClick = onDeleteHistory, enabled = !state.deleting, modifier = Modifier.fillMaxWidth()) { Text("Excluir todo histórico pessoal") }
        state.message?.let { Text(it) }
    }
}
