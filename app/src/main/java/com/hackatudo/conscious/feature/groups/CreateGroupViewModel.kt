package com.hackatudo.conscious.feature.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hackatudo.conscious.domain.repository.StudyGroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CreateGroupUiState(
    val name: String = "",
    val description: String = "",
    val objective: String = "",
    val ownerAlias: String = "",
    val mascotType: String = "Capivara",
    val errorMessage: String? = null,
)

@HiltViewModel
class CreateGroupViewModel @Inject constructor(
    private val groups: StudyGroupRepository,
) : ViewModel() {
    private val mutableState = MutableStateFlow(CreateGroupUiState())
    val uiState: StateFlow<CreateGroupUiState> = mutableState
    private val createdChannel = Channel<UUID>(Channel.BUFFERED)
    val created = createdChannel.receiveAsFlow()

    fun updateName(value: String) = mutableState.update { it.copy(name = value, errorMessage = null) }
    fun updateDescription(value: String) = mutableState.update { it.copy(description = value) }
    fun updateObjective(value: String) = mutableState.update { it.copy(objective = value, errorMessage = null) }
    fun updateOwnerAlias(value: String) = mutableState.update { it.copy(ownerAlias = value, errorMessage = null) }
    fun selectMascot(value: String) = mutableState.update { it.copy(mascotType = value) }

    fun create() {
        val state = mutableState.value
        if (state.name.isBlank() || state.objective.isBlank() || state.ownerAlias.isBlank()) {
            mutableState.update { it.copy(errorMessage = "Informe nome, objetivo e seu apelido.") }
            return
        }
        viewModelScope.launch {
            runCatching { groups.create(state.name, state.description, state.objective, state.ownerAlias, state.mascotType) }
                .onSuccess { createdChannel.send(it.id) }
                .onFailure { mutableState.update { it.copy(errorMessage = "Não foi possível criar o grupo.") } }
        }
    }
}
