package com.hackatudo.conscious.feature.groups

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import java.util.UUID

@Composable
fun GroupsScreen(
    state: GroupsUiState,
    onCreateGroup: () -> Unit,
    onOpenGroup: (UUID) -> Unit,
    onInviteCodeChange: (String) -> Unit,
    onJoinByCode: () -> Unit,
) {
    if (state.isLoading) {
        CircularProgressIndicator()
        return
    }
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Grupos de estudo")
        Button(onClick = onCreateGroup, modifier = Modifier.fillMaxWidth()) { Text("Criar grupo") }
        OutlinedTextField(
            value = state.inviteCode,
            onValueChange = onInviteCodeChange,
            label = { Text("Código demonstrativo") },
            supportingText = { Text("Use MATEMATICA para entrar no primeiro grupo local.") },
            modifier = Modifier.fillMaxWidth(),
        )
        Button(onClick = onJoinByCode, modifier = Modifier.fillMaxWidth()) { Text("Entrar com código") }
        state.message?.let { Text(it) }
        state.groups.forEach { group ->
            Column(
                Modifier.fillMaxWidth().clickable { onOpenGroup(group.id) }.padding(vertical = 12.dp)
                    .semantics { contentDescription = "Abrir grupo ${group.name}" },
            ) {
                Text(group.name)
                Text(group.objective)
                Text("${group.members.count { it.status.name == "ACTIVE" }} participante(s)")
            }
        }
    }
}
