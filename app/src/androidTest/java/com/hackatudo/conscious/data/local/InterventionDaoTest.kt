package com.hackatudo.conscious.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hackatudo.conscious.core.database.AppDatabase
import com.hackatudo.conscious.data.local.dao.FocusSessionRecord
import com.hackatudo.conscious.data.local.entity.FocusSessionEntity
import com.hackatudo.conscious.data.local.entity.SessionEventEntity
import com.hackatudo.conscious.domain.model.FocusSessionStatus
import com.hackatudo.conscious.domain.model.InterventionDecision
import com.hackatudo.conscious.domain.model.ReflectionReason
import com.hackatudo.conscious.domain.model.SessionEventType
import java.util.UUID
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InterventionDaoTest {
    @Test
    fun persistsPrivateDecisionAndOptionalReasonOnlyInsideTheSession() = runTest {
        val database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        ).build()
        val sessionId = UUID.randomUUID()
        database.focusSessionDao().replace(
            FocusSessionRecord(
                FocusSessionEntity(
                    sessionId,
                    "Matemática",
                    1_000,
                    1,
                    FocusSessionStatus.ACTIVE,
                    1,
                    null,
                    0,
                    null,
                    null,
                ),
                emptyList(),
                emptyList(),
            ),
        )
        val withoutReason = SessionEventEntity(
            id = UUID.randomUUID(),
            sessionId = sessionId,
            type = SessionEventType.INTERVENTION_DECIDED,
            occurredAtEpochMillis = 2,
            targetPackage = "chat",
            decision = InterventionDecision.STAY_FOCUSED,
            reflectionReason = null,
            intentId = null,
        )
        val withReason = withoutReason.copy(
            id = UUID.randomUUID(),
            occurredAtEpochMillis = 3,
            decision = InterventionDecision.OPEN_ANYWAY,
            reflectionReason = ReflectionReason.NEEDED_FOR_ACTIVITY,
        )

        database.sessionEventDao().insert(withoutReason)
        database.sessionEventDao().insert(withReason)

        val events = database.sessionEventDao().getForSession(sessionId)
        assertEquals(2, events.size)
        assertNull(events.first().reflectionReason)
        assertEquals(ReflectionReason.NEEDED_FOR_ACTIVITY, events.last().reflectionReason)
        database.close()
    }
}
