package com.hackatudo.conscious.data.demo

import com.hackatudo.conscious.domain.model.ContentSource
import com.hackatudo.conscious.domain.model.FocusContext
import com.hackatudo.conscious.domain.model.group.GoalMetric
import com.hackatudo.conscious.domain.model.group.GroupGoal
import com.hackatudo.conscious.domain.model.group.GroupMember
import com.hackatudo.conscious.domain.model.group.GroupRole
import com.hackatudo.conscious.domain.model.group.Mascot
import com.hackatudo.conscious.domain.model.group.MascotStage
import com.hackatudo.conscious.domain.model.group.StudyGroup
import java.util.UUID

object DemoSeed {
    private val groupId = UUID.fromString("10000000-0000-0000-0000-000000000001")
    private val ownerId = UUID.fromString("10000000-0000-0000-0000-000000000002")

    fun mathematicsContext() = FocusContext(
        id = UUID.fromString("10000000-0000-0000-0000-000000000003"),
        name = "Estudar Matemática",
        suggestedIntention = "Resolver exercícios de frações",
        source = ContentSource.STUDENT,
    )

    fun mathematicsMission(nowEpochMillis: Long = 1_789_156_800_000L): StudyGroup {
        val owner = GroupMember(ownerId, groupId, "Você", GroupRole.OWNER, joinedAtEpochMillis = nowEpochMillis)
        return StudyGroup(
            id = groupId,
            name = "Missão Matemática",
            description = "Progresso coletivo de demonstração",
            objective = "Concluir sessões de estudo",
            ownerMembershipId = owner.id,
            mascot = Mascot(UUID.fromString("10000000-0000-0000-0000-000000000004"), groupId, "Capivara", MascotStage.INITIAL, 0),
            members = listOf(owner),
            goal = GroupGoal(UUID.fromString("10000000-0000-0000-0000-000000000005"), groupId, "5 sessões", GoalMetric.NUMBER_OF_SESSIONS, 5, startsAtEpochMillis = nowEpochMillis, endsAtEpochMillis = nowEpochMillis + 604_800_000L),
            createdAtEpochMillis = nowEpochMillis,
        )
    }

    val mascotStages = listOf(MascotStage.INITIAL, MascotStage.GROWING, MascotStage.EVOLVED)

    data class State(val context: FocusContext, val group: StudyGroup, val stages: List<MascotStage>)

    /** Recria sempre os mesmos dados locais para uma demonstração repetível. */
    fun reset(): State = State(
        context = mathematicsContext(),
        group = mathematicsMission(),
        stages = mascotStages,
    )
}
