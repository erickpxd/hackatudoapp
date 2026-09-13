package com.hackatudo.conscious.data.repository

import androidx.room.withTransaction
import com.hackatudo.conscious.core.database.AppDatabase
import com.hackatudo.conscious.core.time.Clock
import com.hackatudo.conscious.data.local.group.GroupContributionEntity
import com.hackatudo.conscious.data.local.group.GroupGoalEntity
import com.hackatudo.conscious.data.local.group.GroupMemberEntity
import com.hackatudo.conscious.data.local.group.MascotEntity
import com.hackatudo.conscious.data.local.group.StudyGroupEntity
import com.hackatudo.conscious.data.local.group.StudyGroupRecord
import com.hackatudo.conscious.data.local.sync.SyncOutboxEntity
import com.hackatudo.conscious.domain.model.group.GroupAdministrationResult
import com.hackatudo.conscious.domain.model.group.GroupContribution
import com.hackatudo.conscious.domain.model.group.GroupGoal
import com.hackatudo.conscious.domain.model.group.GroupMember
import com.hackatudo.conscious.domain.model.group.GroupMemberStatus
import com.hackatudo.conscious.domain.model.group.GroupRole
import com.hackatudo.conscious.domain.model.group.Mascot
import com.hackatudo.conscious.domain.model.group.MascotStage
import com.hackatudo.conscious.domain.model.group.StudyGroup
import com.hackatudo.conscious.domain.repository.StudyGroupRepository
import com.hackatudo.conscious.domain.usecase.group.GroupAdministrationUseCase
import com.hackatudo.conscious.domain.usecase.group.GroupGoalProgressUseCase
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomStudyGroupRepository @Inject constructor(
    private val database: AppDatabase,
    private val clock: Clock,
    private val administration: GroupAdministrationUseCase,
    private val goalProgress: GroupGoalProgressUseCase,
) : StudyGroupRepository {
    private val dao get() = database.groupDao()

    override fun observeAll(): Flow<List<StudyGroup>> = dao.observeAll().map { rows -> rows.map(StudyGroupRecord::toDomain) }
    override fun observe(groupId: UUID): Flow<StudyGroup?> = dao.observe(groupId).map { it?.toDomain() }

    override suspend fun create(name: String, description: String, objective: String, ownerAlias: String, mascotType: String): StudyGroup =
        database.withTransaction {
            val groupId = UUID.randomUUID()
            val owner = GroupMember(groupId = groupId, participantAlias = ownerAlias.trim(), role = GroupRole.OWNER, joinedAtEpochMillis = clock.nowEpochMillis())
            val group = StudyGroup(
                id = groupId,
                name = name.trim(),
                description = description.trim(),
                objective = objective.trim(),
                ownerMembershipId = owner.id,
                mascot = Mascot(groupId = groupId, type = mascotType.trim()),
                members = listOf(owner),
                createdAtEpochMillis = clock.nowEpochMillis(),
            )
            save(group)
            group
        }

    override suspend fun addMember(groupId: UUID, participantAlias: String): StudyGroup = database.withTransaction {
        val group = requireGroup(groupId)
        require(group.members.none { it.status == GroupMemberStatus.ACTIVE && it.participantAlias.equals(participantAlias.trim(), true) })
        val updated = group.copy(
            members = group.members + GroupMember(
                groupId = groupId,
                participantAlias = participantAlias.trim(),
                role = GroupRole.MEMBER,
                joinedAtEpochMillis = clock.nowEpochMillis(),
            ),
        )
        save(updated)
        updated
    }

    override suspend fun removeMember(groupId: UUID, membershipId: UUID): GroupAdministrationResult = leave(groupId, membershipId)

    override suspend fun transferAdministration(groupId: UUID, newOwnerMembershipId: UUID): StudyGroup = database.withTransaction {
        administration.transferOwnership(requireGroup(groupId), newOwnerMembershipId).also { save(it) }
    }

    override suspend fun leave(groupId: UUID, membershipId: UUID): GroupAdministrationResult = database.withTransaction {
        val result = administration.leave(requireGroup(groupId), membershipId)
        when (result) {
            is GroupAdministrationResult.Updated -> save(result.group)
            is GroupAdministrationResult.GroupClosed -> dao.deleteGroup(result.groupId)
        }
        result
    }

    override suspend fun setGoal(groupId: UUID, goal: GroupGoal): StudyGroup = database.withTransaction {
        require(goal.groupId == groupId)
        val updated = requireGroup(groupId).copy(goal = goal)
        save(updated)
        updated
    }

    override suspend fun applyContribution(contribution: GroupContribution): StudyGroup = database.withTransaction {
        val group = requireGroup(contribution.groupId)
        val goal = requireNotNull(group.goal) { "O grupo ainda não possui uma meta" }
        val result = goalProgress.apply(goal, group.mascot, contribution, group.contributionIds)
        if (!result.applied) return@withTransaction group
        dao.insertContribution(contribution.toEntity())
        database.syncOutboxDao().enqueue(
            SyncOutboxEntity(
                id = contribution.id,
                groupId = contribution.groupId,
                durationMinutes = if (contribution.kind.name == "ELIGIBLE_DURATION") contribution.amount.toInt() else 0,
                completed = contribution.kind.name == "COMPLETED_SESSION",
                occurredAtEpochMillis = contribution.occurredAtEpochMillis,
            ),
        )
        val updated = group.copy(
            goal = result.goal,
            mascot = result.mascot,
            contributionIds = group.contributionIds + contribution.id,
        )
        save(updated)
        updated
    }

    private suspend fun requireGroup(groupId: UUID) = requireNotNull(dao.get(groupId)?.toDomain())

    private suspend fun save(group: StudyGroup) {
        dao.upsertGroup(group.toEntity())
        dao.upsertMembers(group.members.map(GroupMember::toEntity))
        dao.upsertMascot(group.mascot.toEntity())
        group.goal?.let { dao.upsertGoal(it.toEntity()) }
    }
}

private fun StudyGroup.toEntity() = StudyGroupEntity(id, name, description, objective, ownerMembershipId, createdAtEpochMillis)
private fun GroupMember.toEntity() = GroupMemberEntity(id, groupId, participantAlias, role, status, joinedAtEpochMillis)
private fun Mascot.toEntity() = MascotEntity(id, groupId, type, stage, progressPoints)
private fun GroupGoal.toEntity() = GroupGoalEntity(id, groupId, title, metric, target, currentValue, startsAtEpochMillis, endsAtEpochMillis, status)
private fun GroupContribution.toEntity() = GroupContributionEntity(id, groupId, goalId, kind, amount, occurredAtEpochMillis)

private fun StudyGroupRecord.toDomain(): StudyGroup {
    val mascot = requireNotNull(mascots.singleOrNull())
    return StudyGroup(
        id = group.id,
        name = group.name,
        description = group.description,
        objective = group.objective,
        ownerMembershipId = group.ownerMembershipId,
        mascot = Mascot(mascot.id, mascot.groupId, mascot.type, mascot.stage, mascot.progressPoints),
        members = members.map { GroupMember(it.id, it.groupId, it.participantAlias, it.role, it.status, it.joinedAtEpochMillis) },
        goal = goals.maxByOrNull { it.startsAtEpochMillis }?.let {
            GroupGoal(it.id, it.groupId, it.title, it.metric, it.target, it.currentValue, it.startsAtEpochMillis, it.endsAtEpochMillis, it.status)
        },
        contributionIds = contributions.mapTo(mutableSetOf()) { it.id },
        createdAtEpochMillis = group.createdAtEpochMillis,
    )
}
