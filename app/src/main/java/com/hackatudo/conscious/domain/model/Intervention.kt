package com.hackatudo.conscious.domain.model

import java.util.UUID

enum class InterventionDecision {
    STAY_FOCUSED,
    OPEN_ANYWAY,
}

enum class ReflectionReason {
    NEEDED_FOR_ACTIVITY,
    CONTACT_SOMEONE,
    TAKE_BREAK,
    OPENED_BY_HABIT,
    INTENT_CHANGED,
    OTHER,
}

enum class SessionEventType {
    STARTED,
    PAUSED,
    RESUMED,
    INTERVENTION_SHOWN,
    INTERVENTION_DECIDED,
    INTENT_CHANGED,
    COMPLETED,
    CANCELLED,
}

enum class AppLaunchEvaluation {
    ALLOW,
    INTERVENE,
}

data class SessionEvent(
    val id: UUID = UUID.randomUUID(),
    val sessionId: UUID,
    val type: SessionEventType,
    val occurredAtEpochMillis: Long,
    val targetPackage: String? = null,
    val decision: InterventionDecision? = null,
    val reflectionReason: ReflectionReason? = null,
    val intentId: UUID? = null,
) {
    init {
        require(targetPackage == null || targetPackage.isNotBlank())
        require(type == SessionEventType.INTERVENTION_DECIDED || decision == null)
        require(decision != null || reflectionReason == null)
    }
}
