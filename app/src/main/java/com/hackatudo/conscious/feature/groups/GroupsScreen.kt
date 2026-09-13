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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hackatudo.conscious.core.designsystem.component.GeduBottomBar
import com.hackatudo.conscious.core.designsystem.component.GeduTab
import java.util.UUID

private val GroupsBg = Color(0xFF111111)
private val GroupsCard = Color(0xFF202020)
private val GroupsMuted = Color(0xFF969492)

@Composable
fun GroupsScreen(
    state: GroupsUiState,
    onCreateGroup: () -> Unit,
    onOpenGroup: (UUID) -> Unit,
    onInviteCodeChange: (String) -> Unit,
    onJoinByCode: () -> Unit,
    onNewFriendNameChange: (String) -> Unit = {},
    onAddFriend: () -> Unit = {},
    onToggleFriend: (String) -> Unit = {},
    onStartWithFriends: () -> Unit = {},
    onHome: () -> Unit = {},
    onJourney: () -> Unit = {},
    onProfile: () -> Unit = {},
) {
    var studyStep by remember { mutableIntStateOf(0) }
    var socialSection by remember { mutableIntStateOf(0) }
    Box(Modifier.fillMaxSize().background(GroupsBg)) {
        if (state.isLoading) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        } else {
            if (studyStep > 0) {
                FriendStudyWizard(
                    step = studyStep,
                    friends = state.friends,
                    selected = state.selectedFriends,
                    onToggle = onToggleFriend,
                    onBack = { studyStep-- },
                    onContinue = { if (studyStep == 1) studyStep = 2 else onStartWithFriends() },
                )
            } else Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                    .padding(start = 18.dp, top = 20.dp, end = 18.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Text("AMIGOS", color = MaterialTheme.colorScheme.primary, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.4.sp)
                Text("Estudem juntos.", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                Text("Compartilhem o foco, sem competição ou ranking.", color = GroupsMuted, fontSize = 13.sp)

                SocialSectionSelector(socialSection) { socialSection = it }

                if (socialSection == 0) {
                    Button(onClick = { studyStep = 1 }, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp)) {
                        Text("Começar sessão com amigos", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    val friendShape = RoundedCornerShape(20.dp)
                    Column(
                        Modifier.fillMaxWidth().background(GroupsCard, friendShape)
                            .border(1.dp, Color.White.copy(alpha = .08f), friendShape).padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text("Adicionar amigo", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = state.newFriendName,
                                onValueChange = onNewFriendNameChange,
                                placeholder = { Text("Nome ou apelido") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                            )
                            Button(onClick = onAddFriend, enabled = state.newFriendName.isNotBlank(), modifier = Modifier.height(54.dp)) { Text("+") }
                        }
                        state.friends.forEach { name ->
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(name, color = Color.White, fontSize = 12.sp)
                                Text("Amigo", color = GroupsMuted, fontSize = 10.sp)
                            }
                        }
                    }
                    state.message?.let { Text(it, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp) }
                } else {
                    Button(onClick = onCreateGroup, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp)) {
                        Text("Criar grupo", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    val joinShape = RoundedCornerShape(20.dp)
                    Column(
                        Modifier.fillMaxWidth().background(GroupsCard, joinShape)
                            .border(1.dp, Color.White.copy(alpha = .08f), joinShape).padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text("Entrar em um grupo", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = state.inviteCode,
                            onValueChange = onInviteCodeChange,
                            label = { Text("Código de convite") },
                            supportingText = { Text("Na demonstração, use MATEMATICA.") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )
                        OutlinedButton(onClick = onJoinByCode, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) { Text("Entrar com código") }
                        state.message?.let { Text(it, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp) }
                    }

                    Text("Seus grupos", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    if (state.groups.isEmpty()) {
                        Text("Você ainda não participa de nenhum grupo.", color = GroupsMuted, fontSize = 12.sp)
                    }
                    state.groups.forEach { group ->
                        val cardShape = RoundedCornerShape(18.dp)
                        Column(
                            Modifier.fillMaxWidth().background(GroupsCard, cardShape)
                                .border(1.dp, Color.White.copy(alpha = .08f), cardShape)
                                .clickable { onOpenGroup(group.id) }.padding(16.dp)
                                .semantics { contentDescription = "Abrir grupo ${group.name}" },
                            verticalArrangement = Arrangement.spacedBy(5.dp),
                        ) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(group.name, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                Text("→", color = GroupsMuted)
                            }
                            Text(group.objective, color = GroupsMuted, fontSize = 12.sp)
                            Text("${group.members.count { it.status.name == "ACTIVE" }} participante(s)", color = MaterialTheme.colorScheme.primary, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
        if (studyStep == 0) GeduBottomBar(
            selected = GeduTab.FRIENDS,
            onHome = onHome,
            onFriends = {},
            onJourney = onJourney,
            onProfile = onProfile,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun SocialSectionSelector(selected: Int, onSelected: (Int) -> Unit) {
    val shape = RoundedCornerShape(15.dp)
    Row(
        Modifier.fillMaxWidth().background(GroupsCard, shape).border(1.dp, Color.White.copy(alpha = .08f), shape).padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        listOf("Amigos", "Grupos").forEachIndexed { index, label ->
            val active = selected == index
            Box(
                Modifier.weight(1f).height(42.dp)
                    .background(if (active) MaterialTheme.colorScheme.primary else Color.Transparent, RoundedCornerShape(12.dp))
                    .clickable { onSelected(index) },
                contentAlignment = Alignment.Center,
            ) { Text(label, color = if (active) Color.White else GroupsMuted, fontSize = 12.sp, fontWeight = if (active) FontWeight.Bold else FontWeight.Normal) }
        }
    }
}

@Composable
private fun FriendStudyWizard(
    step: Int,
    friends: List<String>,
    selected: Set<String>,
    onToggle: (String) -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit,
) {
    Column(
        Modifier.fillMaxSize().background(GroupsBg).verticalScroll(rememberScrollState()).padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("‹  Voltar", color = GroupsMuted, fontSize = 12.sp, modifier = Modifier.clickable(onClick = onBack).padding(vertical = 6.dp))
        Text("PASSO $step DE 2", color = MaterialTheme.colorScheme.primary, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
        Text(if (step == 1) "Quem vai estudar com você?" else "Chama a galera.", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.ExtraBold)
        Text(if (step == 1) "Selecione seus amigos para a sessão." else "Confira quem receberá o convite.", color = GroupsMuted, fontSize = 12.sp)
        friends.forEach { name ->
            val chosen = name in selected
            val shape = RoundedCornerShape(16.dp)
            Row(
                Modifier.fillMaxWidth().background(GroupsCard, shape)
                    .border(1.dp, if (chosen) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = .08f), shape)
                    .clickable(enabled = step == 1) { onToggle(name) }.padding(15.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.size(34.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = .14f), RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
                    Text(name.take(1).uppercase(), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(if (step == 1) "Disponível" else "Convite pronto", color = GroupsMuted, fontSize = 10.sp)
                }
                Text(if (chosen) "✓" else "+", color = if (chosen) MaterialTheme.colorScheme.primary else GroupsMuted)
            }
        }
        Spacer(Modifier.height(18.dp))
        Button(onClick = onContinue, enabled = selected.isNotEmpty(), modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp)) {
            Text(if (step == 1) "Continuar com ${selected.size} amigo(s)" else "Enviar convites e iniciar", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}
