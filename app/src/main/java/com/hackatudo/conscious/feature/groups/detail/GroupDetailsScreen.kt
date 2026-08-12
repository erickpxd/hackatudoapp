package com.hackatudo.conscious.feature.groups.detail

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
import com.hackatudo.conscious.domain.model.group.GroupMemberStatus
import com.hackatudo.conscious.domain.model.group.GroupRole
import java.util.UUID

private val DetailBg = Color(0xFF111111)
private val DetailCard = Color(0xFF202020)
private val DetailMuted = Color(0xFF969492)

@Composable
fun GroupDetailsScreen(
    state: GroupDetailsUiState,
    onAddMember: () -> Unit,
    onTransferAdministration: (UUID) -> Unit,
    onRemoveMember: (UUID) -> Unit,
    onCreateGoal: () -> Unit,
    onAddCompletedSession: () -> Unit,
    onLeave: () -> Unit,
    onBack: () -> Unit = {},
) {
    Box(Modifier.fillMaxSize().background(DetailBg)) {
        if (state.isLoading) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
            return@Box
        }
        val group = state.group
        if (group == null) {
            Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Grupo não encontrado.", color = Color.White)
                TextButton(onClick = onBack) { Text("Voltar") }
            }
            return@Box
        }
        val accent = MaterialTheme.colorScheme.primary
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text("‹  Voltar", color = DetailMuted, fontSize = 12.sp, modifier = Modifier.clickable(onClick = onBack).padding(vertical = 6.dp))
            Text("GRUPO DE ESTUDO", color = accent, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.4.sp)
            Text(group.name, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            if (group.description.isNotBlank()) Text(group.description, color = DetailMuted, fontSize = 13.sp)

            val focusShape = RoundedCornerShape(20.dp)
            Column(
                Modifier.fillMaxWidth().background(DetailCard, focusShape).border(1.dp, Color.White.copy(alpha = .08f), focusShape).padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Text("OBJETIVO COLETIVO", color = DetailMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(group.objective, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            val goalShape = RoundedCornerShape(20.dp)
            Column(
                Modifier.fillMaxWidth().background(DetailCard, goalShape).border(1.dp, Color.White.copy(alpha = .08f), goalShape).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text("Meta do grupo", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                group.goal?.let { goal ->
                    Text(goal.title, color = DetailMuted, fontSize = 12.sp)
                    LinearProgressIndicator(progress = { (goal.currentValue.toFloat() / goal.target).coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth().height(5.dp), color = accent, trackColor = Color.White.copy(alpha = .10f))
                    Text("${goal.currentValue} de ${goal.target} sessões coletivas", color = accent, fontSize = 11.sp)
                    OutlinedButton(onClick = onAddCompletedSession, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) { Text("Adicionar sessão demonstrativa") }
                } ?: Button(onClick = onCreateGoal, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                    Text("Criar meta de 3 sessões", color = Color.White)
                }
            }

            Text("Participantes", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            group.members.filter { it.status == GroupMemberStatus.ACTIVE }.forEach { member ->
                val memberShape = RoundedCornerShape(16.dp)
                Column(
                    Modifier.fillMaxWidth().background(DetailCard, memberShape).border(1.dp, Color.White.copy(alpha = .08f), memberShape).padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(member.participantAlias, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(if (member.role == GroupRole.OWNER) "Administrador" else "Participante", color = if (member.role == GroupRole.OWNER) accent else DetailMuted, fontSize = 10.sp)
                    }
                    if (member.role != GroupRole.OWNER) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(onClick = { onTransferAdministration(member.id) }) { Text("Tornar administrador", fontSize = 10.sp) }
                            TextButton(onClick = { onRemoveMember(member.id) }) { Text("Remover", color = MaterialTheme.colorScheme.error, fontSize = 10.sp) }
                        }
                    }
                }
            }
            OutlinedButton(onClick = onAddMember, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(14.dp)) { Text("Adicionar participante demonstrativo") }
            state.message?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }

            Text("Não há ranking nem histórico individual de aplicativos, intervenções ou mensagens.", color = DetailMuted, fontSize = 11.sp, lineHeight = 16.sp)
            OutlinedButton(
                onClick = onLeave,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            ) { Text(if (group.members.firstOrNull { it.id == group.ownerMembershipId }?.role == GroupRole.OWNER) "Encerrar e sair do grupo" else "Sair do grupo") }
            Spacer(Modifier.height(12.dp))
        }
    }
}
