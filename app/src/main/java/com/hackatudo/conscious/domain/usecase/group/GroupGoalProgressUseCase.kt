package com.hackatudo.conscious.domain.usecase.group

import com.hackatudo.conscious.domain.model.group.ContributionKind
import com.hackatudo.conscious.domain.model.group.GroupContribution
import com.hackatudo.conscious.domain.model.group.GroupGoal
import com.hackatudo.conscious.domain.model.group.GroupGoalStatus
import com.hackatudo.conscious.domain.model.group.GroupProgressResult
import com.hackatudo.conscious.domain.model.group.Mascot
import com.hackatudo.conscious.domain.usecase.mascot.MascotProgressCalculator
import java.util.UUID
import javax.inject.Inject

class GroupGoalProgressUseCase @Inject constructor(
    private val mascotProgress: MascotProgressCalculator,
) {
    fun apply(
        goal: GroupGoal,
        mascot: Mascot,
        contribution: GroupContribution,
        processedContributionIds: Set<UUID>,
    ): GroupProgressResult {
        require(goal.groupId == contribution.groupId && mascot.groupId == contribution.groupId)
        if (contribution.id in processedContributionIds) return GroupProgressResult(goal, mascot, false)
        val increment = if (contribution.kind == ContributionKind.COMPLETED_SESSION) contribution.amount else 0
        val value = (goal.currentValue + increment).coerceAtMost(goal.target)
        val updatedGoal = goal.copy(
            currentValue = value,
            status = if (value >= goal.target) GroupGoalStatus.ACHIEVED else goal.status,
        )
        val points = mascotProgress.addProgress(mascot.progressPoints, increment * XP_PER_COMPLETED_SESSION)
        val updatedMascot = mascot.copy(progressPoints = points, stage = mascotProgress.stageFor(points))
        return GroupProgressResult(updatedGoal, updatedMascot, true)
    }

    companion object {
        const val XP_PER_COMPLETED_SESSION = 50L
    }
}
