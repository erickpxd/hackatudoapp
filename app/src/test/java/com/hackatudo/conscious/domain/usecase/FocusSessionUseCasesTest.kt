package com.hackatudo.conscious.domain.usecase

import com.hackatudo.conscious.core.time.Clock
import com.hackatudo.conscious.domain.model.FocusSession
import com.hackatudo.conscious.domain.model.FocusSessionStatus
import com.hackatudo.conscious.domain.repository.FocusSessionRepository
import com.hackatudo.conscious.domain.usecase.session.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.UUID

class FocusSessionUseCasesTest {
    private class FakeClock(var value: Long = 100) : Clock { override fun nowEpochMillis() = value }
    private class FakeRepository : FocusSessionRepository {
        val current = MutableStateFlow<FocusSession?>(null)
        private val values = mutableMapOf<UUID, FocusSession>()
        override fun observeCurrent(): Flow<FocusSession?> = current
        override suspend fun get(id: UUID) = values[id]
        override suspend fun save(session: FocusSession) {
            values[session.id] = session
            current.value = session.takeIf { it.status == FocusSessionStatus.ACTIVE || it.status == FocusSessionStatus.PAUSED }
        }
    }

    @Test fun `runs lifecycle and records conscious intention history`() = runTest {
        val repository = FakeRepository()
        val clock = FakeClock()
        val planned = CreateFocusSessionUseCase(repository, clock)("Matemática", 30_000, setOf("calculator"))
        StartFocusSessionUseCase(repository, clock)(planned.id)
        clock.value = 200
        ChangeSessionIntentionUseCase(repository, clock)(planned.id, "Falar com o grupo")
        PauseFocusSessionUseCase(repository, clock)(planned.id)
        clock.value = 300
        ResumeFocusSessionUseCase(repository, clock)(planned.id)
        CompleteFocusSessionUseCase(repository, clock)(planned.id)
        val completed = repository.get(planned.id)!!
        assertEquals(FocusSessionStatus.COMPLETED, completed.status)
        assertEquals(2, completed.intentions.size)
        assertEquals(100, completed.accumulatedPauseMillis)
        assertEquals(null, GetCurrentSessionUseCase(repository)().first())
    }

    @Test fun `cancels a planned session`() = runTest {
        val repository = FakeRepository()
        val clock = FakeClock()
        val session = CreateFocusSessionUseCase(repository, clock)("Leitura", 1_000, emptySet())
        assertEquals(FocusSessionStatus.CANCELLED, CancelFocusSessionUseCase(repository, clock)(session.id).status)
    }
}
