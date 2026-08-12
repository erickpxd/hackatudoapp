package com.hackatudo.conscious.feature.settings

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hackatudo.conscious.R
import com.hackatudo.conscious.core.designsystem.component.GeduBottomBar
import com.hackatudo.conscious.core.designsystem.component.GeduTab
import com.hackatudo.conscious.core.launcher.HomeRoleStatus

private val ProfileBg = Color(0xFF111111)
private val ProfileCard = Color(0xFF202020)
private val ProfileMuted = Color(0xFF969492)

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onDeleteHistory: () -> Unit,
    onUsageAccess: () -> Unit,
    onThemeColorChange: (String) -> Unit = {},
    onNotificationsChange: (Boolean) -> Unit = {},
    onBreakRemindersChange: (Boolean) -> Unit = {},
    onContextChange: (String) -> Unit = {},
    onDistractingChange: (String, Boolean) -> Unit = { _, _ -> },
    onAppNotificationsChange: (String, Boolean) -> Unit = { _, _ -> },
    onRequestHomeRole: () -> Unit = {},
    onHome: () -> Unit = {},
    onFriends: () -> Unit = {},
    onJourney: () -> Unit = {},
) {
    var expanded by remember { mutableStateOf<String?>(null) }
    Box(Modifier.fillMaxSize().background(ProfileBg)) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(start = 18.dp, top = 20.dp, end = 18.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text("PERFIL", color = MaterialTheme.colorScheme.primary, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.4.sp)
            Text("Seu espaço.", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            IdentityCard(state)

            Text("Cor do GEDU", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            ThemeColors(state.themeColor, onThemeColorChange)

            LauncherSettingCard(state.homeRoleStatus, onRequestHomeRole)

            Text("CONTEXTO ATUAL", color = ProfileMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.1.sp)
            ContextSelector(state.currentContext, onContextChange)

            SettingsRow(Icons.Outlined.Notifications, "Notificações", if (state.currentContext == "school") "Bloqueadas no contexto escolar" else "Escolha quais aplicativos podem notificar", expanded == "notifications") { expanded = expanded.toggle("notifications") }
            if (expanded == "notifications") {
                ContextRuleNotice(state.currentContext, "Na escola, todas as notificações de outros aplicativos ficam desativadas.")
                state.apps.forEach { app ->
                    SettingsToggle(
                        app.displayName,
                        state.currentContext != "school" && app.packageName !in state.mutedNotificationPackages,
                        enabled = state.currentContext != "school",
                    ) { onAppNotificationsChange(app.packageName, it) }
                }
            }

            SettingsRow(Icons.Outlined.Apps, "Apps durante o foco", if (state.currentContext == "school") "Apps distrativos não são liberados" else "Escolha quais aplicativos podem ser acessados", expanded == "apps") { expanded = expanded.toggle("apps") }
            if (expanded == "apps") {
                ContextRuleNotice(state.currentContext, "Na escola, aplicativos fora da atividade sempre passam pela pausa consciente.")
                state.apps.forEach { app ->
                    val allowed = state.currentContext != "school" && app.packageName !in state.distractingPackages
                    SettingsToggle(app.displayName, allowed, enabled = state.currentContext != "school") {
                        onDistractingChange(app.packageName, !it)
                    }
                }
                TextButton(onClick = onUsageAccess, modifier = Modifier.fillMaxWidth()) { Text("Configurar acesso aos dados de uso") }
            }

            SettingsRow(Icons.Outlined.Lock, "Privacidade", "Seus dados são somente seus", expanded == "privacy") { expanded = expanded.toggle("privacy") }
            if (expanded == "privacy") {
                InfoCard("Intenções, motivos e histórico pessoal ficam neste aparelho. O painel institucional recebe apenas totais coletivos.")
                OutlinedButton(onClick = onDeleteHistory, enabled = !state.deleting, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                    Text(if (state.deleting) "Excluindo..." else "Excluir histórico pessoal")
                }
            }

            SettingsRow(Icons.Outlined.Info, "Sobre o produto", "Autonomia digital para estudantes", expanded == "about") { expanded = expanded.toggle("about") }
            if (expanded == "about") InfoCard("GEDU ajuda você a estudar com intenção. O pet acompanha seu progresso sem ranking, punição ou julgamento.")
            state.message?.let { Text(it, color = ProfileMuted, fontSize = 12.sp) }
        }
        GeduBottomBar(
            selected = GeduTab.PROFILE,
            onHome = onHome,
            onFriends = onFriends,
            onJourney = onJourney,
            onProfile = {},
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun LauncherSettingCard(status: HomeRoleStatus, onClick: () -> Unit) {
    val enabled = status == HomeRoleStatus.NOT_SELECTED
    val subtitle = when (status) {
        HomeRoleStatus.SELECTED -> "Ativa — ambiente de foco aplicado ao telefone"
        HomeRoleStatus.NOT_SELECTED -> "Organize seus aplicativos com menos cores e distrações"
        HomeRoleStatus.UNSUPPORTED -> "Não disponível neste aparelho"
    }
    val shape = RoundedCornerShape(18.dp)
    Row(
        Modifier.fillMaxWidth().background(ProfileCard, shape)
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = .18f), shape)
            .clickable(enabled = enabled, onClick = onClick).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(42.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = .13f), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
            Icon(Icons.Outlined.Home, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(13.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text("Tela inicial GEDU", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = ProfileMuted, fontSize = 10.sp, lineHeight = 14.sp)
        }
        if (status == HomeRoleStatus.SELECTED) {
            Icon(Icons.Outlined.CheckCircle, "Ativa", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        } else if (enabled) {
            Icon(Icons.AutoMirrored.Outlined.ArrowForward, "Configurar", tint = ProfileMuted, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun IdentityCard(state: SettingsUiState) {
    val shape = RoundedCornerShape(20.dp)
    Row(Modifier.fillMaxWidth().background(ProfileCard, shape).border(1.dp, Color.White.copy(alpha = .08f), shape).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Image(painterResource(profilePetImage(state.petName)), state.petName, Modifier.size(76.dp), contentScale = ContentScale.Fit)
        Spacer(Modifier.width(14.dp))
        Column {
            Text(state.userName.ifBlank { "Estudante" }, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Estudante · ${state.petName}", color = ProfileMuted, fontSize = 12.sp)
        }
    }
}

@Composable
private fun ThemeColors(selected: String, onSelected: (String) -> Unit) {
    val colors = listOf("red" to Color(0xFFFF334D), "blue" to Color(0xFF0A84FF), "green" to Color(0xFF27AE60), "purple" to Color(0xFF9B51E0), "orange" to Color(0xFFFF8A00), "pink" to Color(0xFFFF4F9A))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        colors.forEach { (id, color) ->
            Box(Modifier.size(42.dp).clip(CircleShape).background(color).then(if (selected == id) Modifier.border(3.dp, Color.White, CircleShape) else Modifier).clickable { onSelected(id) })
        }
    }
}

@Composable
private fun ContextSelector(selected: String, onSelected: (String) -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Row(Modifier.fillMaxWidth().background(ProfileCard, shape).border(1.dp, Color.White.copy(alpha = .08f), shape).padding(5.dp), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        listOf("school" to "Na escola", "outside" to "Fora da escola").forEach { (id, label) ->
            val active = selected == id
            Box(Modifier.weight(1f).height(44.dp).background(if (active) MaterialTheme.colorScheme.primary.copy(alpha = .18f) else Color.Transparent, RoundedCornerShape(12.dp)).border(1.dp, if (active) MaterialTheme.colorScheme.primary else Color.Transparent, RoundedCornerShape(12.dp)).clickable { onSelected(id) }, contentAlignment = Alignment.Center) {
                Text(label, color = if (active) MaterialTheme.colorScheme.primary else ProfileMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SettingsRow(icon: ImageVector, title: String, subtitle: String, isOpen: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(18.dp)
    Row(Modifier.fillMaxWidth().background(ProfileCard, shape).border(1.dp, Color.White.copy(alpha = .08f), shape).clickable(onClick = onClick).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(13.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = ProfileMuted, fontSize = 10.sp)
        }
        Icon(Icons.AutoMirrored.Outlined.ArrowForward, if (isOpen) "Fechar" else "Abrir", tint = ProfileMuted, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun SettingsToggle(label: String, checked: Boolean, enabled: Boolean = true, onCheckedChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().background(ProfileCard, RoundedCornerShape(14.dp)).padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = if (enabled) Color.White else ProfileMuted, fontSize = 12.sp, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange, enabled = enabled)
    }
}

@Composable
private fun ContextRuleNotice(context: String, schoolMessage: String) {
    Text(
        if (context == "school") schoolMessage else "Fora da escola, você decide aplicativo por aplicativo.",
        color = ProfileMuted,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary.copy(alpha = .08f), RoundedCornerShape(14.dp)).padding(14.dp),
    )
}

@Composable
private fun InfoCard(text: String) = Text(text, color = ProfileMuted, fontSize = 12.sp, lineHeight = 17.sp, modifier = Modifier.fillMaxWidth().background(ProfileCard, RoundedCornerShape(14.dp)).padding(15.dp))

private fun String?.toggle(value: String) = if (this == value) null else value
@DrawableRes
private fun profilePetImage(id: String) = when (id.lowercase()) {
    "capy" -> R.drawable.capy; "drako" -> R.drawable.drako; "lupy" -> R.drawable.lupy; "mizu" -> R.drawable.mizu
    "pipo" -> R.drawable.pipo; "robo" -> R.drawable.robo; "zy" -> R.drawable.zy; else -> R.drawable.neko
}
