package com.hackatudo.conscious.feature.groups.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hackatudo.conscious.R
import com.hackatudo.conscious.domain.model.group.GroupMemberStatus
import com.hackatudo.conscious.domain.model.group.GroupRole
import com.hackatudo.conscious.domain.model.group.MascotStage
import java.util.UUID

@Composable
fun GroupDetailsScreen(
    state: GroupDetailsUiState,
    onAddMember: () -> Unit,
    onTransferAdministration: (UUID) -> Unit,
    onRemoveMember: (UUID) -> Unit,
    onCreateGoal: () -> Unit,
    onAddCompletedSession: () -> Unit,
    onLeave: () -> Unit,
) {
    if (state.isLoading) {
        CircularProgressIndicator()
        return
    }
    val group = state.group ?: run {
        Text("Grupo não encontrado.")
        return
    }
    val (image, description) = when (group.mascot.stage) {
        MascotStage.INITIAL -> R.drawable.mascot_initial to R.string.mascot_initial_description
        MascotStage.GROWING -> R.drawable.mascot_growing to R.string.mascot_growing_description
        MascotStage.EVOLVED -> R.drawable.mascot_evolved to R.string.mascot_evolved_description
    }
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(group.name)
        Text(group.description)
        Text("Objetivo: ${group.objective}")
        Image(painterResource(image), contentDescription = stringResource(description))
        Text("Mascote ${group.mascot.type}: estágio ${group.mascot.stage.name.lowercase()}")
        Text("XP coletivo: ${group.mascot.progressPoints}. O progresso nunca diminui.")
        group.goal?.let { goal ->
            Text("Meta: ${goal.title}")
            LinearProgressIndicator(
                progress = { (goal.currentValue.toFloat() / goal.target).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth(),
            )
            Text("${goal.currentValue} de ${goal.target} sessões coletivas")
            Button(onClick = onAddCompletedSession) { Text("Adicionar sessão demonstrativa") }
        } ?: Button(onClick = onCreateGoal) { Text("Criar meta de 3 sessões") }
        Text("Participantes")
        group.members.filter { it.status == GroupMemberStatus.ACTIVE }.forEach { member ->
            Text("${member.participantAlias} — ${if (member.role == GroupRole.OWNER) "Administrador" else "Participante"}")
            if (member.role != GroupRole.OWNER) {
                OutlinedButton(onClick = { onTransferAdministration(member.id) }) {
                    Text("Transferir administração para ${member.participantAlias}")
                }
                OutlinedButton(onClick = { onRemoveMember(member.id) }) {
                    Text("Remover ${member.participantAlias}")
                }
            }
        }
        Button(onClick = onAddMember) { Text("Adicionar participante demonstrativo") }
        OutlinedButton(onClick = onLeave) { Text("Sair do grupo") }
        Text("Não há ranking nem histórico individual de apps, intervenções ou mensagens.")
        state.message?.let { Text(it) }
    }
}
