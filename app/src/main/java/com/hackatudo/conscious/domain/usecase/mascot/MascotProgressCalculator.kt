package com.hackatudo.conscious.domain.usecase.mascot

import com.hackatudo.conscious.domain.model.group.MascotStage
import javax.inject.Inject

class MascotProgressCalculator @Inject constructor() {
    fun stageFor(points: Long): MascotStage = when {
        points >= 250 -> MascotStage.EVOLVED
        points >= 100 -> MascotStage.GROWING
        else -> MascotStage.INITIAL
    }

    fun addProgress(currentPoints: Long, earnedPoints: Long): Long =
        currentPoints.coerceAtLeast(0) + earnedPoints.coerceAtLeast(0)
}
