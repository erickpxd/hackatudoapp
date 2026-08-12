package com.hackatudo.conscious.feature.intervention

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val accent = MaterialTheme.colorScheme.primary
    val muted = Color(0xFF9C9CA5)
    Column(
        Modifier.fillMaxSize().background(Color(0xFF111315)).padding(horizontal = 28.dp, vertical = 34.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Precisa", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 38.sp)
        Text("acessar?", color = accent, fontSize = 34.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 38.sp)
        Spacer(Modifier.height(10.dp))
        Text(
            "Escolha por quanto tempo e volte\na estudar com mais foco.",
            color = muted,
            fontSize = 15.sp,
            lineHeight = 21.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(34.dp))

        AccessOption("…", "Mandar uma mensagem", "5 minutos", enabled = !state.isSubmitting) {
            onReasonSelected(ReflectionReason.CONTACT_SOMEONE)
            onOpenAnyway()
        }
        Spacer(Modifier.height(10.dp))
        AccessOption("♨", "Pausa rápida", "10 minutos", enabled = !state.isSubmitting) {
            onReasonSelected(ReflectionReason.TAKE_BREAK)
            onOpenAnyway()
        }
        Spacer(Modifier.height(10.dp))
        AccessOption("!", "Emergência", "30 minutos", enabled = !state.isSubmitting) {
            onReasonSelected(ReflectionReason.OTHER)
            onOpenAnyway()
        }

        state.errorMessage?.let {
            Spacer(Modifier.height(12.dp))
            Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, textAlign = TextAlign.Center)
        }
        Spacer(Modifier.height(30.dp))
        Button(
            onClick = onStayFocused,
            enabled = !state.isSubmitting,
            modifier = Modifier.fillMaxWidth().height(58.dp),
            shape = RoundedCornerShape(18.dp),
        ) {
            Text("⌂", color = Color.White, fontSize = 20.sp)
            Spacer(Modifier.width(10.dp))
            Text("Voltar a estudar", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(12.dp))
        Text("Você estava estudando ${state.intention}.", color = muted.copy(alpha = .72f), fontSize = 10.sp, textAlign = TextAlign.Center)
    }
}

@Composable
private fun AccessOption(icon: String, title: String, duration: String, enabled: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(18.dp)
    Row(
        Modifier.fillMaxWidth().height(84.dp).background(Color(0xFF202326), shape)
            .border(1.dp, Color.White.copy(alpha = .09f), shape)
            .clickable(enabled = enabled, onClick = onClick).padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(38.dp).border(2.dp, Color.White.copy(alpha = .9f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) { Text(icon, color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(duration, color = Color(0xFFAAAAB2), fontSize = 13.sp)
        }
    }
}
