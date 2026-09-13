package com.hackatudo.conscious.domain.usecase

import com.hackatudo.conscious.domain.model.group.ContributionKind
import com.hackatudo.conscious.domain.model.group.GoalMetric
import com.hackatudo.conscious.domain.model.group.GroupContribution
import com.hackatudo.conscious.domain.model.group.GroupGoal
import com.hackatudo.conscious.domain.model.group.GroupGoalStatus
import com.hackatudo.conscious.domain.model.group.Mascot
import com.hackatudo.conscious.domain.model.group.MascotStage
import com.hackatudo.conscious.domain.usecase.group.GroupGoalProgressUseCase
import com.hackatudo.conscious.domain.usecase.mascot.MascotProgressCalculator
import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GroupGoalProgressUseCaseTest {
    private val useCase = GroupGoalProgressUseCase(MascotProgressCalculator())

    @Test
    fun `completed sessions achieve goal once and award transparent xp`() {
        val groupId = UUID.randomUUID()
        val goal = GroupGoal(
            UUID.randomUUID(),
            groupId,
            "Três sessões",
            GoalMetric.NUMBER_OF_SESSIONS,
            1,
            0,
            0,
            10_000,
            GroupGoalStatus.ACTIVE,
        )
        val mascot = Mascot(UUID.randomUUID(), groupId, "Capivara", MascotStage.INITIAL, 75)
        val contribution = GroupContribution(
            UUID.randomUUID(),
            groupId,
            goal.id,
            ContributionKind.COMPLETED_SESSION,
            1,
            1,
        )

        val applied = useCase.apply(goal, mascot, contribution, emptySet())
        val duplicate = useCase.apply(applied.goal, applied.mascot, contribution, setOf(contribution.id))

        assertTrue(applied.applied)
        assertEquals(GroupGoalStatus.ACHIEVED, applied.goal.status)
        assertEquals(125, applied.mascot.progressPoints)
        assertEquals(MascotStage.GROWING, applied.mascot.stage)
        assertFalse(duplicate.applied)
        assertEquals(applied.goal, duplicate.goal)
        assertEquals(applied.mascot, duplicate.mascot)
    }
}
