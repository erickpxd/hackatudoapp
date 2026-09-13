package com.hackatudo.conscious.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingScreen(onContinue: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Use o celular com intenção", modifier = Modifier.semantics { heading() })
        Text("Este launcher organiza seus aplicativos em torno de uma intenção e de sessões que você mesmo cria.")
        Text("Se um toque sair do contexto, a intervenção oferece uma pausa: continuar focado ou abrir mesmo assim continua sendo sua decisão.")
        Text("Intenções, aplicativos, motivos e histórico ficam neste aparelho. Categorias locais não são compartilhadas; somente categorias agregadas e estritamente necessárias podem sair do dispositivo. Você pode apagar o histórico nas configurações.")
        Text("Grupos usam metas coletivas e um mascote que evolui sem ranking ou punição.")
        Text("Somente contribuição, grupo, duração, conclusão e horário podem ser sincronizados. O painel institucional mostra apenas totais.")
        Text("Para usar o botão Home, o Android mostrará uma escolha de tela inicial depois desta explicação. Você pode recusar ou mudar depois.")
        Button(onClick = onContinue, modifier = Modifier.fillMaxWidth()) { Text("Entendi e quero continuar") }
    }
}
