package com.hackatudo.conscious.data.demo

data class InstitutionalAggregate(
    val classroomName: String,
    val sessionCount: Int,
    val averageDurationMinutes: Int,
    val completionRatePercent: Int,
    val interventionCount: Int,
    val trend: String,
)

object InstitutionalAggregateFixtures {
    val mathematics = InstitutionalAggregate(
        classroomName = "Turma 8º A",
        sessionCount = 42,
        averageDurationMinutes = 31,
        completionRatePercent = 86,
        interventionCount = 9,
        trend = "Mais sessões concluídas nas últimas duas semanas",
    )
}
