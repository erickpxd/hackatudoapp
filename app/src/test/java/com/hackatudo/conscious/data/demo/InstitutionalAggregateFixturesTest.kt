package com.hackatudo.conscious.data.demo

import org.junit.Assert.assertTrue
import org.junit.Test

class InstitutionalAggregateFixturesTest {
    @Test
    fun mathematicsFixtureContainsOnlyClassroomAggregates() {
        val aggregate = InstitutionalAggregateFixtures.mathematics

        assertTrue(aggregate.classroomName.isNotBlank())
        assertTrue(aggregate.sessionCount > 0)
        assertTrue(aggregate.averageDurationMinutes > 0)
        assertTrue(aggregate.completionRatePercent in 0..100)
        assertTrue(aggregate.interventionCount >= 0)
        assertTrue(aggregate.trend.isNotBlank())
    }
}
