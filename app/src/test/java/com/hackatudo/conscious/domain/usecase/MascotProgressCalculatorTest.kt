package com.hackatudo.conscious.domain.usecase

import com.hackatudo.conscious.domain.model.group.MascotStage
import com.hackatudo.conscious.domain.usecase.mascot.MascotProgressCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class MascotProgressCalculatorTest {
    private val calculator = MascotProgressCalculator()

    @Test
    fun `uses three documented thresholds`() {
        assertEquals(MascotStage.INITIAL, calculator.stageFor(0))
        assertEquals(MascotStage.INITIAL, calculator.stageFor(99))
        assertEquals(MascotStage.GROWING, calculator.stageFor(100))
        assertEquals(MascotStage.GROWING, calculator.stageFor(249))
        assertEquals(MascotStage.EVOLVED, calculator.stageFor(250))
    }

    @Test
    fun `progress never decreases`() {
        assertEquals(250, calculator.addProgress(currentPoints = 250, earnedPoints = -100))
        assertEquals(300, calculator.addProgress(currentPoints = 250, earnedPoints = 50))
    }
}
