package com.hackatudo.conscious.data.demo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Test

class DemoSeedTest {
    @Test
    fun resetProducesTheSameDemonstrationState() {
        val first = DemoSeed.reset()
        val second = DemoSeed.reset()

        assertNotSame(first, second)
        assertEquals(first.context, second.context)
        assertEquals(first.group, second.group)
        assertEquals(first.stages, second.stages)
        assertEquals("Estudar Matemática", first.context.name)
        assertEquals("Missão Matemática", first.group.name)
        assertEquals(5L, first.group.goal?.target)
        assertEquals(3, first.stages.size)
    }
}
