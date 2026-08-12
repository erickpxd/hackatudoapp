package com.hackatudo.conscious.feature.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hackatudo.conscious.core.designsystem.component.GeduBottomBar
import com.hackatudo.conscious.core.designsystem.component.GeduTab
import com.hackatudo.conscious.domain.model.PersonalInsights
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

private val JourneyBg = Color(0xFF111111)
private val JourneyCard = Color(0xFF202020)
private val JourneyMuted = Color(0xFF969492)

@Composable
fun PersonalInsightsScreen(
    state: PersonalInsightsUiState,
    onHome: () -> Unit = {},
    onFriends: () -> Unit = {},
    onProfile: () -> Unit = {},
) {
    Box(Modifier.fillMaxSize().background(JourneyBg)) {
        if (state.isLoading) CircularProgressIndicator(Modifier.align(Alignment.Center))
        else JourneyContent(state.insights)
        GeduBottomBar(GeduTab.JOURNEY, onHome, onFriends, {}, onProfile, Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun JourneyContent(insights: PersonalInsights) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(start = 18.dp, top = 20.dp, end = 18.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("JORNADA", color = MaterialTheme.colorScheme.primary, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.4.sp)
        Text("Seu rendimento.", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.semantics { heading() })
        Text("Entenda seu ritmo sem competir com ninguém.", color = JourneyMuted, fontSize = 13.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricCard("Sessões", insights.sessionCount.toString(), "registradas", Modifier.weight(1f))
            MetricCard("Interrupções", insights.interventionCount.toString(), "durante o foco", Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricCard("Tempo total", formatMinutes(insights.totalDurationMillis), "de estudo", Modifier.weight(1f))
            MetricCard("Foco médio", formatMinutes(insights.averageDurationMillis), "por sessão", Modifier.weight(1f))
        }
        WeeklyFocusCard(insights)
        InsightCard(insights)
        PatternsCard(insights)
    }
}

@Composable
private fun MetricCard(label: String, value: String, detail: String, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(18.dp)
    Column(modifier.background(JourneyCard, shape).border(1.dp, Color.White.copy(alpha = .07f), shape).padding(14.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(label.uppercase(), color = JourneyMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = .8.sp)
        Text(value, color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.ExtraBold)
        Text(detail, color = JourneyMuted, fontSize = 10.sp)
    }
}

@Composable
private fun WeeklyFocusCard(insights: PersonalInsights) {
    val today = System.currentTimeMillis() / 86_400_000L
    val values = (6 downTo 0).map { offset -> insights.daily.firstOrNull { it.epochDay == today - offset }?.totalDurationMillis ?: 0L }
    val max = values.maxOrNull()?.coerceAtLeast(1L) ?: 1L
    val labels = (6 downTo 0).map { offset ->
        SimpleDateFormat("EEEEE", Locale.forLanguageTag("pt-BR")).format(Date((today - offset) * 86_400_000L)).uppercase()
    }
    JourneyCardContainer {
        Text("FOCO NOS ÚLTIMOS 7 DIAS", color = JourneyMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = .8.sp)
        Row(Modifier.fillMaxWidth().height(96.dp), horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.Bottom) {
            values.forEachIndexed { index, value ->
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
                    Box(Modifier.width(18.dp).height((8 + 58 * (value.toFloat() / max)).dp).background(if (value > 0) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = .07f), RoundedCornerShape(8.dp)))
                    Spacer(Modifier.height(6.dp))
                    Text(labels[index], color = if (index == 6) MaterialTheme.colorScheme.primary else JourneyMuted, fontSize = 8.sp, maxLines = 1)
                }
            }
        }
        Text("${insights.currentConsistencyDays} dia(s) de consistência recente", color = JourneyMuted, fontSize = 10.sp)
    }
}

@Composable
private fun InsightCard(insights: PersonalInsights) {
    val message = when {
        insights.sessionCount == 0 -> "Complete sua primeira sessão para começar a entender seu ritmo."
        insights.currentConsistencyDays >= 3 -> "Você está criando uma rotina. Continue respeitando seu ritmo para manter a consistência."
        insights.averageDurationMillis >= 30 * 60_000L -> "Sessões mais longas parecem fazer parte do seu ritmo. Lembre-se de pausar quando precisar."
        else -> "Sessões curtas também contam. Tente repetir esse tempo em mais um dia da semana."
    }
    val shape = RoundedCornerShape(18.dp)
    Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary.copy(alpha = .10f), shape).border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = .25f), shape).padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Icon(Icons.Outlined.AutoAwesome, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Uma percepção sobre você", color = MaterialTheme.colorScheme.primary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(message, color = Color.White, fontSize = 13.sp, lineHeight = 18.sp)
        }
    }
}

@Composable
private fun PatternsCard(insights: PersonalInsights) {
    JourneyCardContainer {
        Text("PADRÕES OBSERVADOS", color = JourneyMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = .8.sp)
        PatternRow(Icons.Outlined.CheckCircle, "${insights.activeDayCount} dia(s) com estudo registrado")
        PatternRow(Icons.Outlined.Schedule, "Sua sessão média dura ${formatMinutes(insights.averageDurationMillis)}")
        PatternRow(Icons.Outlined.AutoAwesome, if (insights.interventionCount == 0) "Nenhuma interrupção registrada" else "${insights.interventionCount} pausa(s) consciente(s) registrada(s)")
    }
}

@Composable
private fun PatternRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(17.dp))
        Text(text, color = Color.White.copy(alpha = .82f), fontSize = 12.sp)
    }
}

@Composable
private fun JourneyCardContainer(content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(20.dp)
    Column(Modifier.fillMaxWidth().background(JourneyCard, shape).border(1.dp, Color.White.copy(alpha = .07f), shape).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp), content = content)
}

private fun formatMinutes(millis: Long): String = if (millis <= 0) "0 min" else "${(millis / 60_000f).roundToInt().coerceAtLeast(1)} min"
