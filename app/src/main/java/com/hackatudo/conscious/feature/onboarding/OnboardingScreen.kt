package com.hackatudo.conscious.feature.onboarding

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hackatudo.conscious.R

private val Bg = Color(0xFF111111)
private val Card = Color(0xFF202020)
private val LocalAccent = staticCompositionLocalOf { Color(0xFF0A84FF) }
private val TextPrimary = Color(0xFFF5F2ED)
private val TextMuted = Color(0xFF9A9895)

private enum class OnboardingStep { Welcome, Pet, Color, Name, Explanation, Ready }

private data class ThemeOption(val id: String, val name: String, val color: Color)
private val themeOptions = listOf(
    ThemeOption("blue", "Azul", Color(0xFF0A84FF)),
    ThemeOption("red", "Vermelho", Color(0xFFFF334D)),
    ThemeOption("green", "Verde", Color(0xFF27AE60)),
    ThemeOption("purple", "Roxo", Color(0xFF9B51E0)),
    ThemeOption("orange", "Laranja", Color(0xFFFF8A00)),
    ThemeOption("pink", "Rosa", Color(0xFFFF4F9A)),
)

private data class Pet(
    val id: String,
    val displayName: String,
    val trait: String,
    val description: String,
    @DrawableRes val image: Int,
)

private val pets = listOf(
    Pet("neko", "Neko", "Observador", "Nada escapa aos seus olhos", R.drawable.neko),
    Pet("capy", "Capy", "Calmo", "Tranquilo mesmo sob pressão", R.drawable.capy),
    Pet("zy", "Zy", "Curioso", "Sempre explorando novas ideias", R.drawable.zy),
    Pet("robo", "Robo", "Lógico", "Preciso, confiável e direto", R.drawable.robo),
    Pet("lupy", "Lupy", "Energético", "Velocidade com propósito", R.drawable.lupy),
    Pet("pipo", "Pipo", "Gentil", "Paciente e acolhedor", R.drawable.pipo),
    Pet("mizu", "Mizu", "Intenso", "Quando foca, vai fundo", R.drawable.mizu),
    Pet("drako", "Drako", "Sonhador", "Grandes ideias nascem devagar", R.drawable.drako),
)

@Composable
fun OnboardingScreen(onComplete: (String, String, String) -> Unit) {
    var stepName by rememberSaveable { mutableStateOf(OnboardingStep.Welcome.name) }
    var selectedPetId by rememberSaveable { mutableStateOf("neko") }
    var userName by rememberSaveable { mutableStateOf("") }
    var selectedThemeId by rememberSaveable { mutableStateOf("blue") }
    val step = OnboardingStep.valueOf(stepName)
    val selectedPet = pets.first { it.id == selectedPetId }

    val accent = themeOptions.first { it.id == selectedThemeId }.color
    CompositionLocalProvider(LocalAccent provides accent) {
        Surface(color = Bg, contentColor = TextPrimary, modifier = Modifier.fillMaxSize()) {
            when (step) {
                OnboardingStep.Welcome -> WelcomeStep { stepName = OnboardingStep.Pet.name }
                OnboardingStep.Pet -> PetStep(selectedPetId, { selectedPetId = it }) { stepName = OnboardingStep.Color.name }
                OnboardingStep.Color -> ColorStep(selectedThemeId, { selectedThemeId = it }) { stepName = OnboardingStep.Name.name }
                OnboardingStep.Name -> NameStep(selectedPet, userName, { userName = it.take(24) }) { stepName = OnboardingStep.Explanation.name }
                OnboardingStep.Explanation -> ExplanationStep(selectedPet, userName) { stepName = OnboardingStep.Ready.name }
                OnboardingStep.Ready -> ReadyStep(selectedPet, userName) { onComplete(userName, selectedPet.id, selectedThemeId) }
            }
        }
    }
}

@Composable
private fun WelcomeStep(onNext: () -> Unit) = ScreenFrame(bottom = { PrimaryButton("Começar", onNext) }) {
    Text("✳  ✦", color = LocalAccent.current, fontSize = 16.sp)
    Spacer(Modifier.height(34.dp))
    StepLabel("BEM-VINDO")
    Spacer(Modifier.height(12.dp))
    HeroTitle("Seu foco.")
    HeroTitle("Suas\nescolhas.", LocalAccent.current)
    Spacer(Modifier.height(26.dp))
    BodyText("Uma ajudinha para você usar a tecnologia sem deixar que ela escolha por você.")
    Spacer(Modifier.height(28.dp))
    InfoRow("✦", "Não queremos controlar sua atenção.", "Queremos te ensinar a escolher para onde ela vai.")
}

@Composable
private fun PetStep(selectedId: String, onSelect: (String) -> Unit, onNext: () -> Unit) = ScreenFrame(
    bottom = { PrimaryButton("Escolher ${pets.first { it.id == selectedId }.displayName}", onNext) },
) {
    StepLabel("PASSO 1 DE 4")
    Spacer(Modifier.height(10.dp))
    Title("Escolha quem vai\nacompanhar você.")
    BodyText("Seu companheiro de jornada. Ele evolui com você.")
    Spacer(Modifier.height(18.dp))
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxWidth().weight(1f),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(pets) { pet -> PetCard(pet, selectedId == pet.id) { onSelect(pet.id) } }
    }
}

@Composable
private fun PetCard(pet: Pet, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(14.dp)
    Column(
        modifier = Modifier
            .height(168.dp)
            .clip(shape)
            .background(if (selected) LocalAccent.current.copy(alpha = .14f) else Card)
            .border(1.dp, if (selected) LocalAccent.current else Color.Transparent, shape)
            .clickable(onClick = onClick)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(painterResource(pet.image), pet.displayName, Modifier.size(88.dp), contentScale = ContentScale.Fit)
        Text(pet.displayName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Text(pet.trait, color = LocalAccent.current, fontSize = 11.sp)
        Text(pet.description, color = TextMuted, fontSize = 9.sp, lineHeight = 12.sp, textAlign = TextAlign.Center)
    }
}

@Composable
private fun ColorStep(selectedId: String, onSelect: (String) -> Unit, onNext: () -> Unit) = ScreenFrame(
    bottom = { PrimaryButton("Usar esta cor", onNext) },
) {
    StepLabel("PASSO 2 DE 4")
    Spacer(Modifier.height(14.dp))
    Title("Escolha a cor\ndo seu sistema.")
    Spacer(Modifier.height(8.dp))
    BodyText("Você poderá mudar essa escolha depois nas configurações.")
    Spacer(Modifier.height(36.dp))
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        themeOptions.chunked(2).forEach { rowOptions ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowOptions.forEach { option ->
                    val selected = option.id == selectedId
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(72.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Card)
                            .border(2.dp, if (selected) option.color else Color.Transparent, RoundedCornerShape(14.dp))
                            .clickable { onSelect(option.id) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Box(Modifier.size(30.dp).background(option.color, RoundedCornerShape(50)))
                        Text(option.name, fontSize = 12.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }
        }
    }
    Spacer(Modifier.height(34.dp))
    Surface(color = Color(0xFF1B1B1B), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Prévia", color = TextMuted, fontSize = 11.sp)
            Text("Seu foco, do seu jeito.", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Box(Modifier.fillMaxWidth().height(8.dp).background(LocalAccent.current, RoundedCornerShape(50)))
        }
    }
}

@Composable
private fun NameStep(pet: Pet, name: String, onNameChange: (String) -> Unit, onNext: () -> Unit) = ScreenFrame(
    bottom = { PrimaryButton("Continuar", onNext, name.isNotBlank()) },
) {
    val focusManager = LocalFocusManager.current
    StepLabel("PASSO 3 DE 4")
    Spacer(Modifier.height(42.dp))
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        Image(painterResource(pet.image), pet.displayName, Modifier.size(72.dp))
        Surface(color = Color(0xFF2A2A2A), shape = RoundedCornerShape(12.dp)) {
            Text("Como posso te chamar?", Modifier.padding(horizontal = 16.dp, vertical = 13.dp), fontSize = 12.sp)
        }
    }
    Spacer(Modifier.height(24.dp))
    Title("Qual é o seu nome?")
    Spacer(Modifier.height(12.dp))
    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Seu nome ou apelido") },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = LocalAccent.current, cursorColor = LocalAccent.current),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(); if (name.isNotBlank()) onNext() }),
        supportingText = { Text("Só você vai ver isso.", color = TextMuted) },
    )
}

@Composable
private fun ExplanationStep(pet: Pet, name: String, onNext: () -> Unit) = ScreenFrame(
    bottom = { PrimaryButton("Entendi. Vamos lá!", onNext) },
) {
    StepLabel("PASSO 4 DE 4")
    Spacer(Modifier.height(22.dp))
    Title("Oi, ${name.trim()}!")
    BodyText("Deixa eu te explicar como isso funciona.")
    Image(painterResource(pet.image), pet.displayName, Modifier.size(150.dp).align(Alignment.CenterHorizontally))
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        InfoRow("◉", "Consciência", "Você vai perceber suas próprias distrações")
        InfoRow("Ⅱ", "Pausa", "Antes de desviar, você pensa por um segundo")
        InfoRow("✦", "Escolha", "Você decide. Sempre.")
    }
    Spacer(Modifier.height(22.dp))
    Surface(
        color = LocalAccent.current.copy(alpha = .10f),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, LocalAccent.current.copy(alpha = .25f)),
    ) {
        Text(
            "“Eu não vou decidir por você. Quando algo tentar tirar seu foco, vou te ajudar a parar um segundo e pensar.”\n\n— ${pet.displayName}",
            Modifier.padding(16.dp),
            fontSize = 12.sp,
            lineHeight = 18.sp,
            color = TextPrimary,
        )
    }
}

@Composable
private fun ReadyStep(pet: Pet, name: String, onFinish: () -> Unit) = ScreenFrame(
    bottom = { PrimaryButton("Vamos começar ✦", onFinish) },
) {
    Spacer(Modifier.height(18.dp))
    Text("✦  ✳  ✦", color = LocalAccent.current, fontSize = 17.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
    Spacer(Modifier.height(18.dp))
    Title("Pronto.", Modifier.align(Alignment.CenterHorizontally))
    Text("${name.trim()} e ${pet.displayName} — prontos pra começar.", color = TextMuted, fontSize = 13.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
    Spacer(Modifier.height(54.dp))
    Image(painterResource(pet.image), pet.displayName, Modifier.fillMaxWidth().height(280.dp), contentScale = ContentScale.Fit)
}

@Composable
private fun ScreenFrame(bottom: @Composable () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 24.dp)) {
        Column(Modifier.weight(1f), content = content)
        bottom()
        Spacer(Modifier.height(10.dp))
    }
}

@Composable private fun StepLabel(text: String) = Text(text, color = LocalAccent.current, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.6.sp)
@Composable private fun HeroTitle(text: String, color: Color = TextPrimary) = Text(text, color = color, fontSize = 40.sp, lineHeight = 40.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.semantics { heading() })
@Composable private fun Title(text: String, modifier: Modifier = Modifier) = Text(text, modifier.semantics { heading() }, color = TextPrimary, fontSize = 27.sp, lineHeight = 30.sp, fontWeight = FontWeight.ExtraBold)
@Composable private fun BodyText(text: String) = Text(text, color = TextMuted, fontSize = 13.sp, lineHeight = 19.sp)

@Composable
private fun InfoRow(icon: String, title: String, description: String) = Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
    Box(Modifier.size(38.dp).background(Card, RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) { Text(icon, color = LocalAccent.current) }
    Column(Modifier.weight(1f)) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Text(description, color = TextMuted, fontSize = 11.sp, lineHeight = 15.sp)
    }
}

@Composable
private fun PrimaryButton(text: String, onClick: () -> Unit, enabled: Boolean = true) = Button(
    onClick = onClick,
    enabled = enabled,
    modifier = Modifier.fillMaxWidth().height(56.dp),
    shape = RoundedCornerShape(14.dp),
    colors = ButtonDefaults.buttonColors(containerColor = LocalAccent.current, contentColor = Color.White, disabledContainerColor = Card, disabledContentColor = TextMuted),
) { Text(text, fontWeight = FontWeight.Bold, fontSize = 14.sp) }
