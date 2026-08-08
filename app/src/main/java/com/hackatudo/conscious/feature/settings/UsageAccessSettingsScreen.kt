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
fun UsageAccessSettingsScreen(isAvailable: Boolean, hasPermission: Boolean, onOpenSystemSettings: () -> Unit, onDecline: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Indicador pessoal opcional")
        Text("O acesso de uso pode calcular tempos somente neste aparelho. Ele não é necessário para launcher, sessões ou grupos e nunca é sincronizado.")
        Text(if (!isAvailable) "Este recurso não está disponível." else if (hasPermission) "Acesso concedido nas configurações do Android." else "Acesso não concedido.")
        Button(onClick = onOpenSystemSettings, enabled = isAvailable, modifier = Modifier.fillMaxWidth()) { Text("Abrir configurações do Android") }
        Button(onClick = onDecline, modifier = Modifier.fillMaxWidth()) { Text("Agora não") }
    }
}
