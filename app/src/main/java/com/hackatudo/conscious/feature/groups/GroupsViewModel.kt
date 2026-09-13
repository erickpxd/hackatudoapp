package com.hackatudo.conscious.feature.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hackatudo.conscious.domain.model.group.StudyGroup
import com.hackatudo.conscious.domain.repository.StudyGroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GroupsUiState(
    val isLoading: Boolean = true,
    val groups: List<StudyGroup> = emptyList(),
    val inviteCode: String = "",
    val message: String? = null,
)

sealed interface GroupsEffect {
    data class OpenGroup(val groupId: UUID) : GroupsEffect
}

@HiltViewModel
class GroupsViewModel @Inject constructor(
    private val groups: StudyGroupRepository,
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
