package com.hackatudo.conscious.feature.groups

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val CreateGroupBg = Color(0xFF111111)
private val CreateGroupCard = Color(0xFF202020)
private val CreateGroupMuted = Color(0xFF969492)

@Composable
fun CreateGroupScreen(
    state: CreateGroupUiState,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onObjectiveChange: (String) -> Unit,
    onOwnerAliasChange: (String) -> Unit,
    onCreate: () -> Unit,
    onBack: () -> Unit = {},
) {
    Column(
        Modifier.fillMaxSize().background(CreateGroupBg).verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("‹  Voltar", color = CreateGroupMuted, fontSize = 12.sp, modifier = Modifier.clickable(onClick = onBack).padding(vertical = 6.dp))
        Text("NOVO GRUPO", color = MaterialTheme.colorScheme.primary, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.4.sp)
        Text("Criar grupo de estudo", color = Color.White, fontSize = 27.sp, fontWeight = FontWeight.ExtraBold)
        Text("Defina um objetivo para vocês avançarem juntos, sem ranking ou punição.", color = CreateGroupMuted, fontSize = 13.sp, lineHeight = 18.sp)

        val formShape = RoundedCornerShape(20.dp)
        Column(
            Modifier.fillMaxWidth().background(CreateGroupCard, formShape)
                .border(1.dp, Color.White.copy(alpha = .08f), formShape).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            GroupField(state.name, onNameChange, "Nome do grupo", "Ex.: Foco no vestibular")
            GroupField(state.description, onDescriptionChange, "Descrição", "Sobre o que é este grupo?", minLines = 2)
            GroupField(state.objective, onObjectiveChange, "Objetivo coletivo", "Ex.: Estudar juntos três vezes por semana", minLines = 2)
            GroupField(state.ownerAlias, onOwnerAliasChange, "Como seus amigos verão você", "Nome ou apelido")
        }

        state.errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }
        Spacer(Modifier.height(4.dp))
        Button(
            onClick = onCreate,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(15.dp),
        ) { Text("Criar grupo", color = Color.White, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun GroupField(value: String, onValueChange: (String) -> Unit, label: String, placeholder: String, minLines: Int = 1) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        modifier = Modifier.fillMaxWidth(),
        minLines = minLines,
        singleLine = minLines == 1,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF191919),
            unfocusedContainerColor = Color(0xFF191919),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = Color.White.copy(alpha = .10f),
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = CreateGroupMuted,
        ),
    )
}
