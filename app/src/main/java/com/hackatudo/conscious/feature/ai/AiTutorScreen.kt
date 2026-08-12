package com.hackatudo.conscious.feature.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private const val DailyQuestionLimit = 5

@Composable
fun AiTutorScreen(onBack: () -> Unit) {
    var input by remember { mutableStateOf("") }
    var questionsUsed by remember { mutableIntStateOf(0) }
    var messages by remember { mutableStateOf(listOf("Conte o que você está tentando entender. Em vez de entregar a resposta, vou ajudar você a pensar.")) }
    val accent = MaterialTheme.colorScheme.primary

    fun send() {
        if (input.isBlank() || questionsUsed >= DailyQuestionLimit) return
        val question = input.trim()
        val prompts = listOf(
            "O que você já sabe sobre “$question”?",
            "Qual parte dessa pergunta parece mais difícil para você?",
            "Que hipótese você tentaria primeiro?",
            "Como você explicaria esse problema com suas próprias palavras?",
            "Que evidência ajudaria você a conferir sua resposta?",
        )
        messages = messages + "Você: $question" + "IA socrática: ${prompts[questionsUsed]}"
        questionsUsed++
        input = ""
    }

    Column(Modifier.fillMaxSize().background(Color(0xFF111111)).padding(18.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = onBack) { Text("← Voltar") }
            Text("$questionsUsed/$DailyQuestionLimit perguntas", color = Color(0xFF969492), fontSize = 11.sp)
        }
        Text("IA socrática", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
        Text("Ela pergunta para ajudar você a construir a resposta.", color = Color(0xFF969492), fontSize = 12.sp)
        Spacer(Modifier.height(18.dp))
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            messages.forEach { message ->
                Surface(color = Color(0xFF202020), shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
                    Text(message, Modifier.padding(14.dp), color = Color.White, fontSize = 13.sp, lineHeight = 19.sp)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = input,
            onValueChange = { input = it.take(280) },
            modifier = Modifier.fillMaxWidth(),
            enabled = questionsUsed < DailyQuestionLimit,
            placeholder = { Text(if (questionsUsed < DailyQuestionLimit) "Faça uma pergunta" else "Limite diário atingido") },
            trailingIcon = { TextButton(onClick = ::send, enabled = input.isNotBlank() && questionsUsed < DailyQuestionLimit) { Text("Enviar", color = accent) } },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = { send() }),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = accent, cursorColor = accent),
        )
    }
}
