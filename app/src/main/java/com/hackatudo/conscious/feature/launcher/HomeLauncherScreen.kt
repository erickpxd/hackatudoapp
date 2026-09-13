package com.hackatudo.conscious.feature.launcher

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PsychologyAlt
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hackatudo.conscious.R
import com.hackatudo.conscious.core.designsystem.component.ErrorState
import com.hackatudo.conscious.core.designsystem.component.GeduBottomBar
import com.hackatudo.conscious.core.designsystem.component.GeduTab
import com.hackatudo.conscious.core.launcher.HomeRoleStatus

private val HomeBg = Color(0xFF111111)
private val HomeCard = Color(0xFF202020)
private val HomeMuted = Color(0xFF969492)

@Composable
fun HomeLauncherScreen(
    state: LauncherUiState,
    onAppClick: (String) -> Unit,
    onRequestHomeRole: () -> Unit,
    onDismissHomeRolePrompt: () -> Unit = {},
    onRetry: () -> Unit,
    onNewSession: () -> Unit = {},
    onActiveSession: () -> Unit = {},
    onInsights: () -> Unit = {},
    onGroups: () -> Unit = {},
    onSuggestion: () -> Unit = {},
    onInstitution: () -> Unit = {},
    onSettings: () -> Unit = {},
    onAiTutor: () -> Unit = {},
    onProfile: () -> Unit = {},
) {
    when {
        state.isLoading -> Box(Modifier.fillMaxSize().background(HomeBg), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        state.errorMessage != null -> ErrorState(state.errorMessage, onRetry)
        else -> Box(Modifier.fillMaxSize().background(HomeBg)) {
            HomeContent(state, onRequestHomeRole, onNewSession, onActiveSession, onInsights, onGroups, onAiTutor)
            GeduBottomBar(
                selected = GeduTab.HOME,
                onHome = {},
                onFriends = onGroups,
                onJourney = onInsights,
                onProfile = onProfile,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
            if (state.showHomeRolePrompt) {
                HomeRoleInvitation(
                    onActivate = {
                        onDismissHomeRolePrompt()
                        onRequestHomeRole()
                    },
                    onDismiss = onDismissHomeRolePrompt,
                )
            }
        }
    }
}

@Composable
private fun HomeRoleInvitation(onActivate: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Box(
                Modifier.size(52.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = .14f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.Home, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(27.dp))
            }
        },
        title = { Text("Uma tela inicial para o seu foco", color = Color.White, fontWeight = FontWeight.ExtraBold) },
        text = {
            Text(
                "O GEDU pode organizar os aplicativos com menos cores e ajudar você a fazer escolhas conscientes durante as sessões. Você pode voltar à tela anterior quando quiser.",
                color = HomeMuted,
                lineHeight = 20.sp,
            )
        },
        confirmButton = {
            Button(onClick = onActivate) { Text("Usar tela inicial GEDU", color = Color.White) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Agora não") }
        },
        containerColor = HomeCard,
        shape = RoundedCornerShape(24.dp),
    )
}

@Composable
private fun HomeContent(
    state: LauncherUiState,
    onRequestHomeRole: () -> Unit,
    onNewSession: () -> Unit,
    onActiveSession: () -> Unit,
    onInsights: () -> Unit,
    onGroups: () -> Unit,
    onAiTutor: () -> Unit,
) {
    val accent = MaterialTheme.colorScheme.primary
    val minutes = state.studyMillisToday / 60_000
    val dailyGoal = 60L
    val progress = (minutes.toFloat() / dailyGoal).coerceIn(0f, 1f)
    val petName = state.petName.replaceFirstChar { it.uppercase() }
    val greeting = state.userName.ifBlank { "você" }

    Column(
        Modifier.fillMaxSize().background(HomeBg).verticalScroll(rememberScrollState())
            .padding(start = 18.dp, top = 20.dp, end = 18.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                Image(
                    painter = painterResource(R.drawable.gedu_logo_vertical),
                    contentDescription = "GEDU",
                    modifier = Modifier.width(104.dp).height(42.dp),
                    contentScale = ContentScale.FillBounds,
                    colorFilter = ColorFilter.tint(accent),
                )
            }
            Text("Oi, $greeting!", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.semantics { heading() })
        }

        val petCardShape = RoundedCornerShape(20.dp)
        Surface(
            color = HomeCard,
            shape = petCardShape,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.White.copy(alpha = .08f), petCardShape),
        ) {
            Row(
                Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Image(painterResource(petImage(state.petName)), petName, Modifier.size(96.dp), contentScale = ContentScale.Fit)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    val speechShape = RoundedCornerShape(
                        topStart = 17.dp,
                        topEnd = 17.dp,
                        bottomEnd = 17.dp,
                        bottomStart = 0.dp,
                    )
                    Surface(
                        color = Color(0xFF2A2A2A),
                        shape = speechShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.White.copy(alpha = .08f), speechShape),
                    ) {
                        Text(
                            "Vamos conquistar mais um objetivo hoje?",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                            color = Color.White,
                            fontSize = 15.sp,
                            lineHeight = 20.sp,
                        )
                    }
                    Text(petName, color = HomeMuted, fontSize = 12.sp)
                }
            }
        }

        val progressCardShape = RoundedCornerShape(20.dp)
        Surface(
            color = HomeCard,
            shape = progressCardShape,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.White.copy(alpha = .08f), progressCardShape)
                .clickable(onClick = onInsights),
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("TEMPO DE ESTUDO HOJE", color = accent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("$minutes / $dailyGoal min", color = HomeMuted, fontSize = 11.sp)
                }
                LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape), color = accent, trackColor = Color(0xFF343434))
                Text(
                    if (minutes >= dailyGoal) "$petName está feliz com seu check-in de hoje!" else "Estude ${dailyGoal - minutes} min para completar o check-in de $petName.",
                    color = Color.White,
                    fontSize = 12.sp,
                )
                Text("${state.sessionsToday} ${if (state.sessionsToday == 1) "sessão concluída" else "sessões concluídas"}", color = HomeMuted, fontSize = 10.sp)
            }
        }

        val activeSession = state.currentSession
        val sessionCardShape = RoundedCornerShape(20.dp)
        Surface(
            color = accent,
            shape = sessionCardShape,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.White.copy(alpha = .12f), sessionCardShape)
                .clickable(onClick = if (activeSession == null) onNewSession else onActiveSession),
        ) {
            Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(if (activeSession == null) "Iniciar sessão" else "Continuar sessão", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                    Text(activeSession?.currentIntention?.text ?: activeSession?.title ?: "O que você vai estudar?", color = Color.White.copy(alpha = .82f), fontSize = 12.sp)
                }
                Box(Modifier.size(48.dp).background(Color.Black.copy(alpha = .16f), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) { Text("+", color = Color.White, fontSize = 30.sp) }
            }
        }

        ActionCard(Icons.Outlined.Groups, "Estudar com amigos", "Crie ou entre em um grupo de foco", onGroups)
        ActionCard(Icons.Outlined.PsychologyAlt, "IA socrática", "Perguntas que ajudam você a encontrar a resposta", onAiTutor)

        if (state.homeRoleStatus == HomeRoleStatus.NOT_SELECTED) {
            val homeRoleShape = RoundedCornerShape(18.dp)
            Surface(
                color = Color(0xFF251B1C),
                shape = homeRoleShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, accent.copy(alpha = .22f), homeRoleShape)
                    .clickable(onClick = onRequestHomeRole),
            ) {
                Text("Definir como tela inicial  →", Modifier.padding(15.dp), color = accent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun ActionCard(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    val accent = MaterialTheme.colorScheme.primary
    val cardShape = RoundedCornerShape(20.dp)
    Surface(
        color = HomeCard,
        shape = cardShape,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.White.copy(alpha = .08f), cardShape)
            .clickable(onClick = onClick),
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(13.dp)) {
            Box(Modifier.size(38.dp).background(accent.copy(alpha = .12f), RoundedCornerShape(11.dp)), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(subtitle, color = HomeMuted, fontSize = 11.sp)
            }
            Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = "Abrir", tint = HomeMuted, modifier = Modifier.size(18.dp))
        }
    }
}

@DrawableRes
private fun petImage(id: String) = when (id.lowercase()) {
    "capy" -> R.drawable.capy
    "drako" -> R.drawable.drako
    "lupy" -> R.drawable.lupy
    "mizu" -> R.drawable.mizu
    "pipo" -> R.drawable.pipo
    "robo" -> R.drawable.robo
    "zy" -> R.drawable.zy
    else -> R.drawable.neko
}
