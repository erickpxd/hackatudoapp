package com.hackatudo.conscious.feature.session.active

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hackatudo.conscious.R
import com.hackatudo.conscious.domain.model.FocusSessionStatus

private val ActiveBg = Color(0xFF111111)
private val ActiveCard = Color(0xFF202020)
private val ActiveMuted = Color(0xFF969492)

@Composable
fun ActiveSessionScreen(
    state: ActiveSessionUiState,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onComplete: () -> Unit,
    onCancel: () -> Unit,
    onChangeIntention: (String) -> Unit,
    onAiTutor: () -> Unit = {},
    onOpenPhoneHome: () -> Unit = {},
) {
    val session = state.session ?: return
    val accent = MaterialTheme.colorScheme.primary
    val intention = session.currentIntention?.text ?: session.title
    val parts = intention.substringBefore(":").split(" · ")
    val activity = parts.firstOrNull().orEmpty()
    val subject = parts.getOrNull(1) ?: intention
    val progress = if (session.plannedDurationMillis > 0) {
        (state.remainingMillis.toFloat() / session.plannedDurationMillis).coerceIn(0f, 1f)
    } else 0f

    Column(
        Modifier.fillMaxSize().background(ActiveBg).verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("SESSÃO ATIVA", color = ActiveMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
            Text(if (session.status == FocusSessionStatus.PAUSED) "pausada" else "em foco", color = accent, fontSize = 10.sp)
        }
        Spacer(Modifier.height(24.dp))
        Column(Modifier.fillMaxWidth()) {
            Text(subject.uppercase(), color = accent, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
            Text(activity.ifBlank { "Estudar" }, color = ActiveMuted, fontSize = 12.sp)
        }
        Spacer(Modifier.height(42.dp))

        Box(Modifier.size(180.dp), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxSize(), color = accent, trackColor = Color.White.copy(alpha = .10f), strokeWidth = 7.dp)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(formatRemaining(state.remainingMillis), color = Color.White, fontSize = 38.sp, fontWeight = FontWeight.ExtraBold)
                Text("restante", color = ActiveMuted, fontSize = 10.sp)
            }
        }
        Spacer(Modifier.height(32.dp))

        PetStatus(state.petName, if (session.status == FocusSessionStatus.PAUSED) "Respire um pouco. Eu espero você." else "Você está indo bem.")
        Spacer(Modifier.height(30.dp))

        SessionAction("✦", "Pedir ajuda à IA", "Converse com a IA socrática", onAiTutor)
        Spacer(Modifier.height(10.dp))
        SessionAction("⌂", "Abrir outros aplicativos", "Voltar para a tela inicial do telefone", onOpenPhoneHome)
        Spacer(Modifier.height(22.dp))

        LinearProgressIndicator(progress = { 1f - progress }, modifier = Modifier.fillMaxWidth().height(4.dp), color = accent, trackColor = Color.White.copy(alpha = .10f))
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Progresso", color = ActiveMuted, fontSize = 10.sp)
            Text("${((1f - progress) * 100).toInt()}%", color = ActiveMuted, fontSize = 10.sp)
        }
        Spacer(Modifier.height(18.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
                onClick = if (session.status == FocusSessionStatus.PAUSED) onResume else onPause,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(14.dp),
            ) { Text(if (session.status == FocusSessionStatus.PAUSED) "Retomar" else "Pausar") }
            Button(onClick = onComplete, modifier = Modifier.weight(1f).height(50.dp), shape = RoundedCornerShape(14.dp)) { Text("Concluir") }
        }
        TextButton(onClick = onCancel, modifier = Modifier.padding(top = 4.dp)) { Text("Encerrar sessão", color = ActiveMuted) }
    }
}

@Composable
private fun PetStatus(petName: String, message: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
        Image(painterResource(activePetImage(petName)), petName, Modifier.size(58.dp), contentScale = ContentScale.Fit)
        Spacer(Modifier.width(8.dp))
        val shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp, bottomEnd = 14.dp, bottomStart = 0.dp)
        Surface(color = ActiveCard, shape = shape, modifier = Modifier.border(1.dp, Color.White.copy(alpha = .08f), shape)) {
            Text(message, Modifier.padding(horizontal = 16.dp, vertical = 12.dp), color = Color.White, fontSize = 12.sp)
        }
    }
}

@Composable
private fun SessionAction(icon: String, title: String, subtitle: String, onClick: () -> Unit) {
    val accent = MaterialTheme.colorScheme.primary
    val shape = RoundedCornerShape(18.dp)
    Row(
        Modifier.fillMaxWidth().background(ActiveCard, shape).border(1.dp, Color.White.copy(alpha = .08f), shape)
            .clickable(onClick = onClick).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(40.dp).background(accent.copy(alpha = .16f), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) { Text(icon, color = accent, fontSize = 18.sp) }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = ActiveMuted, fontSize = 10.sp)
        }
        Text("→", color = ActiveMuted)
    }
}

private fun formatRemaining(millis: Long): String {
    val seconds = (millis.coerceAtLeast(0) / 1_000)
    return "%02d:%02d".format(seconds / 60, seconds % 60)
}

@DrawableRes
private fun activePetImage(id: String) = when (id.lowercase()) {
    "capy" -> R.drawable.capy
    "drako" -> R.drawable.drako
    "lupy" -> R.drawable.lupy
    "mizu" -> R.drawable.mizu
    "pipo" -> R.drawable.pipo
    "robo" -> R.drawable.robo
    "zy" -> R.drawable.zy
    else -> R.drawable.neko
}
