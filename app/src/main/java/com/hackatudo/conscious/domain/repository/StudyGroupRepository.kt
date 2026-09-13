package com.hackatudo.conscious.domain.repository

import com.hackatudo.conscious.domain.model.group.GroupAdministrationResult
import com.hackatudo.conscious.domain.model.group.GroupContribution
import com.hackatudo.conscious.domain.model.group.GroupGoal
import com.hackatudo.conscious.domain.model.group.StudyGroup
import java.util.UUID
import kotlinx.coroutines.flow.Flow

interface StudyGroupRepository {
    fun observeAll(): Flow<List<StudyGroup>>
    fun observe(groupId: UUID): Flow<StudyGroup?>
    suspend fun create(
        name: String,
        description: String,
        objective: String,
        ownerAlias: String,
        mascotType: String,
    ): StudyGroup
    suspend fun addMember(groupId: UUID, participantAlias: String): StudyGroup
    suspend fun removeMember(groupId: UUID, membershipId: UUID): GroupAdministrationResult
    suspend fun transferAdministration(groupId: UUID, newOwnerMembershipId: UUID): StudyGroup
    suspend fun leave(groupId: UUID, membershipId: UUID): GroupAdministrationResult
    suspend fun setGoal(groupId: UUID, goal: GroupGoal): StudyGroup
    suspend fun applyContribution(contribution: GroupContribution): StudyGroup
}
