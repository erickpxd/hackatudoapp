package com.hackatudo.conscious.feature.session.create

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hackatudo.conscious.R

private val SessionBg = Color(0xFF111111)
private val SessionCard = Color(0xFF202020)
private val SessionMuted = Color(0xFF969492)

@Composable
fun CreateSessionScreen(
    state: CreateSessionUiState,
    onIntentionChange: (String) -> Unit,
    onDurationChange: (Int) -> Unit,
    onToggleApp: (String) -> Unit,
    onApplyContext: (com.hackatudo.conscious.domain.model.FocusContext) -> Unit,
    onStart: () -> Unit,
    onSaveContext: (String) -> Unit = {},
    onBack: () -> Unit = {},
) {
    var step by rememberSaveable { mutableIntStateOf(1) }
    var activity by rememberSaveable { mutableStateOf("") }
    var subject by rememberSaveable { mutableStateOf("") }
    var details by rememberSaveable { mutableStateOf("") }

    Column(
        Modifier.fillMaxSize().background(SessionBg).verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 16.dp),
    ) {
        Text("‹  Voltar", color = SessionMuted, fontSize = 12.sp, modifier = Modifier.clickable {
            if (step > 1) step-- else onBack()
        }.padding(vertical = 6.dp))
        Spacer(Modifier.height(12.dp))
        WizardProgress(step)
        Spacer(Modifier.height(20.dp))
        when (step) {
            1 -> ActivityStep(state.petName, activity, { activity = it }) { step = 2 }
            2 -> SubjectStep(state.petName, subject, details, { subject = it }, { details = it }) { step = 3 }
            else -> DurationStep(state.petName, activity, subject, state.durationMinutes, onDurationChange, state.error) {
                onIntentionChange(buildString {
                    append(activity); append(" · "); append(subject)
                    if (details.isNotBlank()) append(": ${details.trim()}")
                })
                onStart()
            }
        }
    }
}

@Composable
private fun ActivityStep(petName: String, selected: String, onSelected: (String) -> Unit, onContinue: () -> Unit) {
    Heading("O que você quer fazer?")
    Spacer(Modifier.height(14.dp))
    PetPrompt(petName, "Eu fico com você durante a sessão.")
    Spacer(Modifier.height(18.dp))
    listOf("Estudar", "Fazer atividade", "Ler", "Programar", "Pesquisar", "Assistir aula", "Outro").chunked(2).forEach { row ->
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            row.forEach { choice -> ChoiceBox(choice, selected == choice, Modifier.weight(1f)) { onSelected(choice) } }
            if (row.size == 1) Spacer(Modifier.weight(1f))
        }
        Spacer(Modifier.height(10.dp))
    }
    Spacer(Modifier.height(12.dp))
    PrimaryButton("Continuar", selected.isNotBlank(), onContinue)
}

@Composable
private fun SubjectStep(petName: String, selected: String, details: String, onSubjectSelected: (String) -> Unit, onDetailsChange: (String) -> Unit, onContinue: () -> Unit) {
    Heading("Qual é a matéria?")
    Spacer(Modifier.height(14.dp))
    PetPrompt(petName, "Isso ajuda a organizar seu histórico de estudo.")
    Spacer(Modifier.height(16.dp))
    FlowChoices(listOf("Matemática", "Português", "Física", "Química", "História", "Biologia", "Inglês", "Filosofia"), selected, onSubjectSelected)
    Spacer(Modifier.height(20.dp))
    OutlinedTextField(
        value = details, onValueChange = onDetailsChange, modifier = Modifier.fillMaxWidth(),
        label = { Text("Atividade") }, placeholder = { Text("Descreva a atividade...") }, minLines = 2,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = SessionCard, unfocusedContainerColor = SessionCard,
            focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = Color.White.copy(alpha = .08f),
        ),
    )
    Spacer(Modifier.height(14.dp))
    PrimaryButton("Continuar", selected.isNotBlank(), onContinue)
}

@Composable
private fun DurationStep(petName: String, activity: String, subject: String, selected: Int, onDurationSelected: (Int) -> Unit, error: String?, onStart: () -> Unit) {
    Heading("Quanto tempo?")
    Text("Escolha a duração da sessão.", color = SessionMuted, fontSize = 12.sp)
    Spacer(Modifier.height(14.dp))
    PetPrompt(petName, "Você poderá pausar ou encerrar quando quiser.")
    Spacer(Modifier.height(18.dp))
    listOf(25, 30, 45, 50, 60, 90).chunked(3).forEach { row ->
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            row.forEach { minutes -> DurationBox(minutes, selected == minutes, Modifier.weight(1f)) { onDurationSelected(minutes) } }
        }
        Spacer(Modifier.height(10.dp))
    }
    Spacer(Modifier.height(12.dp))
    val noticeShape = RoundedCornerShape(16.dp)
    Row(
        Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary.copy(alpha = .10f), noticeShape)
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = .22f), noticeShape).padding(14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text("!", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.width(10.dp))
        Column {
            Text("Foco consciente ativo", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(
                "Apps de entretenimento, como YouTube, farão uma pausa para você negociar o tempo antes de abrir.",
                color = SessionMuted, fontSize = 11.sp, lineHeight = 16.sp,
            )
        }
    }
    Spacer(Modifier.height(12.dp))
    val shape = RoundedCornerShape(18.dp)
    Column(Modifier.fillMaxWidth().background(SessionCard, shape).border(1.dp, Color.White.copy(alpha = .08f), shape).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SummaryRow("Atividade", activity)
        SummaryRow("Matéria", subject)
        SummaryRow("Duração", "$selected min", true)
    }
    error?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(top = 10.dp)) }
    Spacer(Modifier.height(14.dp))
    PrimaryButton("Iniciar sessão ✦", true, onStart)
}

@Composable private fun Heading(text: String) = Text(text, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)

@Composable
private fun WizardProgress(step: Int) {
    val accent = MaterialTheme.colorScheme.primary
    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
        Text("PASSO $step DE 3", color = accent, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(3) { index ->
                Box(
                    Modifier.weight(1f).height(3.dp)
                        .background(if (index < step) accent else Color.White.copy(alpha = .10f), RoundedCornerShape(2.dp)),
                )
            }
        }
    }
}

@Composable
private fun PetPrompt(petName: String, message: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Image(painterResource(sessionPetImage(petName)), petName, Modifier.size(44.dp), contentScale = ContentScale.Fit)
        val shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomEnd = 12.dp, bottomStart = 0.dp)
        Surface(color = SessionCard, shape = shape) { Text(message, Modifier.padding(horizontal = 14.dp, vertical = 10.dp), color = Color.White, fontSize = 11.sp) }
    }
}

@Composable
private fun ChoiceBox(label: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val accent = MaterialTheme.colorScheme.primary
    val shape = RoundedCornerShape(14.dp)
    Box(modifier.height(48.dp).background(if (selected) accent.copy(alpha = .18f) else SessionCard, shape).border(1.dp, if (selected) accent else Color.White.copy(alpha = .07f), shape).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Text(label, color = if (selected) accent else Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun FlowChoices(choices: List<String>, selected: String, onSelected: (String) -> Unit) {
    choices.chunked(4).forEach { row ->
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            row.forEach { choice -> FilterChip(selected = selected == choice, onClick = { onSelected(choice) }, label = { Text(choice, fontSize = 10.sp) }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primary)) }
        }
    }
}

@Composable
private fun DurationBox(minutes: Int, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val accent = MaterialTheme.colorScheme.primary
    val shape = RoundedCornerShape(14.dp)
    Column(modifier.height(64.dp).background(SessionCard, shape).border(1.dp, if (selected) accent else Color.White.copy(alpha = .07f), shape).clickable(onClick = onClick), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("$minutes", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text("min", color = SessionMuted, fontSize = 9.sp)
    }
}

@Composable
private fun SummaryRow(label: String, value: String, highlighted: Boolean = false) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = SessionMuted, fontSize = 11.sp)
        Text(value, color = if (highlighted) MaterialTheme.colorScheme.primary else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun PrimaryButton(label: String, enabled: Boolean, onClick: () -> Unit) {
    Button(onClick = onClick, enabled = enabled, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp)) { Text(label, fontWeight = FontWeight.Bold) }
}

@DrawableRes
private fun sessionPetImage(id: String) = when (id.lowercase()) {
    "capy" -> R.drawable.capy
    "drako" -> R.drawable.drako
    "lupy" -> R.drawable.lupy
    "mizu" -> R.drawable.mizu
    "pipo" -> R.drawable.pipo
    "robo" -> R.drawable.robo
    "zy" -> R.drawable.zy
    else -> R.drawable.neko
}
