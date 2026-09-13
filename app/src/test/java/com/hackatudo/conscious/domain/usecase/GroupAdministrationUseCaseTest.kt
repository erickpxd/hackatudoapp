package com.hackatudo.conscious.domain.usecase

import com.hackatudo.conscious.domain.model.group.GroupAdministrationResult
import com.hackatudo.conscious.domain.model.group.GroupMember
import com.hackatudo.conscious.domain.model.group.GroupRole
import com.hackatudo.conscious.domain.model.group.GroupMemberStatus
import com.hackatudo.conscious.domain.model.group.Mascot
import com.hackatudo.conscious.domain.model.group.MascotStage
import com.hackatudo.conscious.domain.model.group.StudyGroup
import com.hackatudo.conscious.domain.usecase.group.GroupAdministrationUseCase
import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GroupAdministrationUseCaseTest {
    private val useCase = GroupAdministrationUseCase()

    @Test
    fun `transfer keeps exactly one active owner`() {
        val group = groupWithMembers("Ana", "Bia")
        val newOwner = group.members.last()

        val updated = useCase.transferOwnership(group, newOwner.id)

        assertEquals(1, updated.members.count { it.status == GroupMemberStatus.ACTIVE && it.role == GroupRole.OWNER })
        assertEquals(newOwner.id, updated.ownerMembershipId)
    }

    @Test
    fun `owner leave elects an eligible member deterministically`() {
        val group = groupWithMembers("Zeca", "Ana", "Bia")
        val owner = group.members.first()

        val result = useCase.leave(group, owner.id) as GroupAdministrationResult.Updated

        assertEquals("Ana", result.group.members.first { it.role == GroupRole.OWNER }.participantAlias)
        assertEquals(GroupMemberStatus.LEFT, result.group.members.first { it.id == owner.id }.status)
    }

    @Test
    fun `last owner leaving returns explicit group closure`() {
        val group = groupWithMembers("Ana")

        val result = useCase.leave(group, group.ownerMembershipId)

        assertTrue(result is GroupAdministrationResult.GroupClosed)
    }

    private fun groupWithMembers(vararg aliases: String): StudyGroup {
        val groupId = UUID.randomUUID()
        val members = aliases.mapIndexed { index, alias ->
            GroupMember(
                id = UUID.randomUUID(),
                groupId = groupId,
                participantAlias = alias,
                role = if (index == 0) GroupRole.OWNER else GroupRole.MEMBER,
                status = GroupMemberStatus.ACTIVE,
                joinedAtEpochMillis = index.toLong(),
            )
        }
        return StudyGroup(
            id = groupId,
            name = "Missão Matemática",
            description = "Estudar juntos",
            objective = "Concluir sessões",
            ownerMembershipId = members.first().id,
            mascot = Mascot(UUID.randomUUID(), groupId, "Capivara", MascotStage.INITIAL, 0),
            members = members,
            createdAtEpochMillis = 0,
        )
    }
}
