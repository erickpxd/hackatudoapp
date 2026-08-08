package com.hackatudo.conscious.feature.groups

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CreateGroupScreen(
    state: CreateGroupUiState,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onObjectiveChange: (String) -> Unit,
    onOwnerAliasChange: (String) -> Unit,
    onMascotSelected: (String) -> Unit,
    onCreate: () -> Unit,
) {
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Criar grupo de estudo")
        OutlinedTextField(state.name, onNameChange, label = { Text("Nome") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(state.description, onDescriptionChange, label = { Text("Descrição") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(state.objective, onObjectiveChange, label = { Text("Objetivo coletivo") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(state.ownerAlias, onOwnerAliasChange, label = { Text("Seu apelido") }, modifier = Modifier.fillMaxWidth())
        Text("Escolha do mascote")
        listOf("Capivara", "Coruja", "Tucano").forEach { mascot ->
            FilterChip(selected = state.mascotType == mascot, onClick = { onMascotSelected(mascot) }, label = { Text(mascot) })
        }
        state.errorMessage?.let { Text(it) }
        Button(onClick = onCreate, modifier = Modifier.fillMaxWidth()) { Text("Criar grupo") }
    }
}
