package com.hackatudo.conscious.domain.model.group

import java.util.UUID

enum class GroupRole { OWNER, MEMBER }
enum class GroupMemberStatus { ACTIVE, LEFT }
enum class MascotStage { INITIAL, GROWING, EVOLVED }
enum class GoalMetric { NUMBER_OF_SESSIONS }
enum class GroupGoalStatus { ACTIVE, ACHIEVED, EXPIRED }
enum class ContributionKind { COMPLETED_SESSION, COLLABORATIVE_ACTIVITY, ACTIVE_DAY, ELIGIBLE_DURATION }

data class GroupMember(
    val id: UUID = UUID.randomUUID(),
    val groupId: UUID,
    val participantAlias: String,
    val role: GroupRole,
    val status: GroupMemberStatus = GroupMemberStatus.ACTIVE,
    val joinedAtEpochMillis: Long,
) {
    init { require(participantAlias.isNotBlank()) }
}

data class Mascot(
    val id: UUID = UUID.randomUUID(),
    val groupId: UUID,
    val type: String,
    val stage: MascotStage = MascotStage.INITIAL,
    val progressPoints: Long = 0,
) {
    init {
        require(type.isNotBlank())
        require(progressPoints >= 0)
    }
}

data class GroupGoal(
    val id: UUID = UUID.randomUUID(),
    val groupId: UUID,
    val title: String,
    val metric: GoalMetric = GoalMetric.NUMBER_OF_SESSIONS,
    val target: Long,
    val currentValue: Long = 0,
    val startsAtEpochMillis: Long,
    val endsAtEpochMillis: Long,
    val status: GroupGoalStatus = GroupGoalStatus.ACTIVE,
) {
    init {
        require(title.isNotBlank())
        require(target > 0)
        require(currentValue >= 0)
        require(endsAtEpochMillis > startsAtEpochMillis)
    }
}

data class GroupContribution(
    val id: UUID = UUID.randomUUID(),
    val groupId: UUID,
    val goalId: UUID? = null,
    val kind: ContributionKind,
    val amount: Long,
    val occurredAtEpochMillis: Long,
) {
    init { require(amount > 0) }
}

data class StudyGroup(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val description: String,
    val objective: String,
    val ownerMembershipId: UUID,
    val mascot: Mascot,
    val members: List<GroupMember>,
    val goal: GroupGoal? = null,
    val contributionIds: Set<UUID> = emptySet(),
    val createdAtEpochMillis: Long,
) {
    init {
        require(name.isNotBlank())
        require(objective.isNotBlank())
        require(mascot.groupId == id)
        require(members.all { it.groupId == id })
        require(members.count { it.status == GroupMemberStatus.ACTIVE && it.role == GroupRole.OWNER } == 1)
        require(members.any { it.id == ownerMembershipId && it.status == GroupMemberStatus.ACTIVE && it.role == GroupRole.OWNER })
    }
}

sealed interface GroupAdministrationResult {
    data class Updated(val group: StudyGroup) : GroupAdministrationResult
    data class GroupClosed(val groupId: UUID) : GroupAdministrationResult
}

data class GroupProgressResult(
    val goal: GroupGoal,
    val mascot: Mascot,
    val applied: Boolean,
)
