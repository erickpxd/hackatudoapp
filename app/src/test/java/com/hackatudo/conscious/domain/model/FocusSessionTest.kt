package com.hackatudo.conscious.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class FocusSessionTest {
    private fun planned() = FocusSession(title = "Estudar", plannedDurationMillis = 1_000, createdAtEpochMillis = 0)

    @Test fun `supports valid lifecycle`() {
        val completed = planned().start(10).pause(20).resume(30).complete(40)
        assertEquals(FocusSessionStatus.COMPLETED, completed.status)
        assertEquals(10, completed.accumulatedPauseMillis)
    }

    @Test fun `rejects transition after final state`() {
        val completed = planned().start(10).complete(20)
        assertThrows(IllegalArgumentException::class.java) { completed.pause(30) }
    }
}
