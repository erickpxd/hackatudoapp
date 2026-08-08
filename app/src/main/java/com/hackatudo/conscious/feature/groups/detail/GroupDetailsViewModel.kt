package com.hackatudo.conscious.feature.groups.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hackatudo.conscious.core.time.Clock
import com.hackatudo.conscious.domain.model.group.ContributionKind
import com.hackatudo.conscious.domain.model.group.GroupAdministrationResult
import com.hackatudo.conscious.domain.model.group.GroupContribution
import com.hackatudo.conscious.domain.model.group.GroupGoal
import com.hackatudo.conscious.domain.model.group.StudyGroup
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

data class GroupDetailsUiState(
    val isLoading: Boolean = true,
    val group: StudyGroup? = null,
    val message: String? = null,
)

@HiltViewModel
class GroupDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val groups: StudyGroupRepository,
    private val clock: Clock,
) : ViewModel() {
    private val groupId = UUID.fromString(checkNotNull(savedStateHandle["groupId"]))
    private val mutableState = MutableStateFlow(GroupDetailsUiState())
    val uiState: StateFlow<GroupDetailsUiState> = mutableState
    private val closedChannel = Channel<Unit>(Channel.BUFFERED)
    val closed = closedChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            groups.observe(groupId).collect { group ->
                mutableState.update { it.copy(isLoading = false, group = group) }
            }
        }
    }

    fun addDemoMember() = runAction { groups.addMember(groupId, "Participante ${requireGroup().members.size + 1}") }

    fun transferAdministration(membershipId: UUID) = runAction { groups.transferAdministration(groupId, membershipId) }

    fun removeMember(membershipId: UUID) = runResultAction { groups.removeMember(groupId, membershipId) }

    fun leaveAsOwner() = runResultAction { groups.leave(groupId, requireGroup().ownerMembershipId) }

    fun createGoal() = runAction {
        val now = clock.nowEpochMillis()
        groups.setGoal(
            groupId,
            GroupGoal(
                groupId = groupId,
                title = "Concluir 3 sessões",
                target = 3,
                startsAtEpochMillis = now,
                endsAtEpochMillis = now + 7 * 86_400_000L,
            ),
        )
    }

    fun addCompletedSession() = runAction {
        val group = requireGroup()
        groups.applyContribution(
            GroupContribution(
                groupId = groupId,
                goalId = requireNotNull(group.goal).id,
                kind = ContributionKind.COMPLETED_SESSION,
                amount = 1,
                occurredAtEpochMillis = clock.nowEpochMillis(),
            ),
        )
    }

    private fun runAction(action: suspend () -> StudyGroup) {
        viewModelScope.launch {
            runCatching { action() }.onFailure { showError() }
        }
    }

    private fun runResultAction(action: suspend () -> GroupAdministrationResult) {
        viewModelScope.launch {
            runCatching { action() }
                .onSuccess { if (it is GroupAdministrationResult.GroupClosed) closedChannel.send(Unit) }
                .onFailure { showError() }
        }
    }

    private fun requireGroup() = requireNotNull(mutableState.value.group)
    private fun showError() = mutableState.update { it.copy(message = "Não foi possível atualizar o grupo.") }
}
