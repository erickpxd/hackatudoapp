package com.hackatudo.conscious.domain.usecase

import com.hackatudo.conscious.domain.model.AppLaunchEvaluation
import com.hackatudo.conscious.domain.model.FocusSession
import com.hackatudo.conscious.domain.model.FocusSessionStatus
import com.hackatudo.conscious.domain.repository.FocusSessionRepository
import com.hackatudo.conscious.domain.usecase.intervention.EvaluateAppLaunchUseCase
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class EvaluateAppLaunchUseCaseTest {
    private class FakeRepository(initial: FocusSession?) : FocusSessionRepository {
        private val current = MutableStateFlow(initial)
        override fun observeCurrent(): Flow<FocusSession?> = current
        override suspend fun get(id: UUID): FocusSession? = current.value?.takeIf { it.id == id }
        override suspend fun save(session: FocusSession) {
            current.value = session.takeIf {
                it.status == FocusSessionStatus.ACTIVE || it.status == FocusSessionStatus.PAUSED
            }
        }
    }

    @Test
    fun `allows launches when there is no current session`() = runTest {
        val evaluation = EvaluateAppLaunchUseCase(FakeRepository(null))("chat")

        assertEquals(AppLaunchEvaluation.ALLOW, evaluation)
    }

    @Test
    fun `allows an app related to the current session`() = runTest {
        val evaluation = EvaluateAppLaunchUseCase(FakeRepository(activeSession(setOf("calculator"))))("calculator")

        assertEquals(AppLaunchEvaluation.ALLOW, evaluation)
    }

    @Test
    fun `intervenes for an app outside the current context`() = runTest {
        val evaluation = EvaluateAppLaunchUseCase(FakeRepository(activeSession(setOf("calculator"))))("chat")

        assertEquals(AppLaunchEvaluation.INTERVENE, evaluation)
    }

    private fun activeSession(packages: Set<String>) = FocusSession(
        title = "Estudar matemática",
        plannedDurationMillis = 30 * 60_000,
        createdAtEpochMillis = 1,
        status = FocusSessionStatus.ACTIVE,
        startedAtEpochMillis = 1,
        selectedPackageNames = packages,
    )
}
