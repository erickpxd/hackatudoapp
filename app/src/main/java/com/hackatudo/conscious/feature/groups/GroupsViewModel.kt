package com.hackatudo.conscious.feature.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hackatudo.conscious.domain.model.group.StudyGroup
import com.hackatudo.conscious.domain.repository.StudyGroupRepository
import com.hackatudo.conscious.domain.usecase.session.GetCurrentSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GroupsUiState(
    val isLoading: Boolean = true,
    val groups: List<StudyGroup> = emptyList(),
    val inviteCode: String = "",
    val message: String? = null,
    val friends: List<String> = listOf("Ana", "Lucas", "Bia", "João", "Marina"),
    val selectedFriends: Set<String> = emptySet(),
    val newFriendName: String = "",
)

sealed interface GroupsEffect {
    data class OpenGroup(val groupId: UUID) : GroupsEffect
    data object CreateGroupSession : GroupsEffect
    data object ResumeActiveSession : GroupsEffect
}

@HiltViewModel
class GroupsViewModel @Inject constructor(
    private val groups: StudyGroupRepository,
    private val currentSession: GetCurrentSessionUseCase,
) : ViewModel() {
    private val form = MutableStateFlow(GroupsUiState())
    private val effectsChannel = Channel<GroupsEffect>(Channel.BUFFERED)
    val effects = effectsChannel.receiveAsFlow()
    val uiState: StateFlow<GroupsUiState> = form

    init {
        viewModelScope.launch {
            groups.observeAll().collect { values -> form.update { it.copy(isLoading = false, groups = values) } }
        }
    }

    fun setInviteCode(value: String) = form.update { it.copy(inviteCode = value, message = null) }
    fun setNewFriendName(value: String) = form.update { it.copy(newFriendName = value, message = null) }
    fun addFriend() = form.update { state ->
        val name = state.newFriendName.trim()
        if (name.isBlank() || state.friends.any { it.equals(name, true) }) {
            state.copy(message = "Informe um nome novo para adicionar.")
        } else {
            state.copy(friends = state.friends + name, newFriendName = "", message = "$name foi adicionado aos seus amigos.")
        }
    }
    fun toggleFriend(name: String) = form.update { state ->
        state.copy(selectedFriends = if (name in state.selectedFriends) state.selectedFriends - name else state.selectedFriends + name)
    }
    fun startWithFriends() = viewModelScope.launch {
        effectsChannel.send(
            if (currentSession().first() == null) GroupsEffect.CreateGroupSession else GroupsEffect.ResumeActiveSession,
        )
    }

    fun joinDemoGroup() {
        viewModelScope.launch {
            val state = form.value
            val group = state.groups.firstOrNull()
            if (state.inviteCode.trim().uppercase() != DEMO_CODE || group == null) {
                form.update { it.copy(message = "Código demonstrativo inválido ou sem grupo disponível.") }
                return@launch
            }
            runCatching { groups.addMember(group.id, "Convidado") }
                .onSuccess { effectsChannel.send(GroupsEffect.OpenGroup(it.id)) }
                .onFailure { form.update { it.copy(message = "Este participante já entrou no grupo.") } }
        }
    }

    companion object { const val DEMO_CODE = "MATEMATICA" }
}
