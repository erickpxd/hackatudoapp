package com.hackatudo.conscious.domain.usecase.group

import com.hackatudo.conscious.domain.model.group.GroupAdministrationResult
import com.hackatudo.conscious.domain.model.group.GroupMemberStatus
import com.hackatudo.conscious.domain.model.group.GroupRole
import com.hackatudo.conscious.domain.model.group.StudyGroup
import java.util.UUID
import javax.inject.Inject

class GroupAdministrationUseCase @Inject constructor() {
    fun transferOwnership(group: StudyGroup, newOwnerMembershipId: UUID): StudyGroup {
        require(newOwnerMembershipId != group.ownerMembershipId)
        require(group.members.any { it.id == newOwnerMembershipId && it.status == GroupMemberStatus.ACTIVE })
        return group.copy(
            ownerMembershipId = newOwnerMembershipId,
            members = group.members.map { member ->
                when (member.id) {
                    group.ownerMembershipId -> member.copy(role = GroupRole.MEMBER)
                    newOwnerMembershipId -> member.copy(role = GroupRole.OWNER)
                    else -> member.copy(role = GroupRole.MEMBER)
                }
            },
        )
    }

    fun leave(group: StudyGroup, membershipId: UUID): GroupAdministrationResult {
        val leaving = requireNotNull(group.members.firstOrNull { it.id == membershipId })
        require(leaving.status == GroupMemberStatus.ACTIVE)
        val eligible = group.members
            .filter { it.id != membershipId && it.status == GroupMemberStatus.ACTIVE }
            .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.participantAlias })
        if (eligible.isEmpty()) return GroupAdministrationResult.GroupClosed(group.id)
        val nextOwner = if (leaving.role == GroupRole.OWNER) eligible.first().id else group.ownerMembershipId
        val updated = group.copy(
            ownerMembershipId = nextOwner,
            members = group.members.map { member ->
                when {
                    member.id == membershipId -> member.copy(status = GroupMemberStatus.LEFT, role = GroupRole.MEMBER)
                    member.id == nextOwner -> member.copy(role = GroupRole.OWNER)
                    else -> member.copy(role = GroupRole.MEMBER)
                }
            },
        )
        return GroupAdministrationResult.Updated(updated)
    }
}
