package com.hackatudo.conscious.domain.usecase.intervention

import com.hackatudo.conscious.core.time.Clock
import com.hackatudo.conscious.domain.model.InterventionDecision
import com.hackatudo.conscious.domain.model.ReflectionReason
import com.hackatudo.conscious.domain.model.SessionEvent
import com.hackatudo.conscious.domain.model.SessionEventType
import com.hackatudo.conscious.domain.repository.SessionEventRepository
import java.util.UUID
import javax.inject.Inject

class RecordInterventionShownUseCase @Inject constructor(
    private val events: SessionEventRepository,
    private val clock: Clock,
) {
    suspend operator fun invoke(sessionId: UUID, packageName: String) {
        events.record(
            SessionEvent(
                sessionId = sessionId,
                type = SessionEventType.INTERVENTION_SHOWN,
                occurredAtEpochMillis = clock.nowEpochMillis(),
                targetPackage = packageName,
            ),
        )
    }
}

class RecordInterventionDecisionUseCase @Inject constructor(
    private val events: SessionEventRepository,
    private val clock: Clock,
) {
    suspend operator fun invoke(
        sessionId: UUID,
        packageName: String,
        decision: InterventionDecision,
        reason: ReflectionReason? = null,
    ) {
        events.record(
            SessionEvent(
                sessionId = sessionId,
                type = SessionEventType.INTERVENTION_DECIDED,
                occurredAtEpochMillis = clock.nowEpochMillis(),
                targetPackage = packageName,
                decision = decision,
                reflectionReason = reason,
            ),
        )
    }
}
