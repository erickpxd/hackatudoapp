package com.hackatudo.conscious

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.core.app.ApplicationProvider
import com.hackatudo.conscious.core.datastore.PrivacyPreferencesDataStore
import com.hackatudo.conscious.domain.model.AppLaunchEvaluation
import com.hackatudo.conscious.domain.model.FocusSession
import com.hackatudo.conscious.domain.model.UsageIntent
import com.hackatudo.conscious.domain.repository.FocusSessionRepository
import com.hackatudo.conscious.domain.usecase.intervention.EvaluateAppLaunchUseCase
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EndToEndOfflineTest {
    @Test fun launcherSessionInterventionAndCompletionNeedNoNetwork() = runBlocking {
        val sessionId = UUID.randomUUID()
        val planned = FocusSession(
            id = sessionId,
            title = "Estudar Matemática",
            plannedDurationMillis = 30 * 60_000L,
            createdAtEpochMillis = 1_000,
            intentions = listOf(UsageIntent(sessionId = sessionId, text = "Revisar frações", createdAtEpochMillis = 1_000)),
            selectedPackageNames = setOf("com.example.calculator"),
        ).start(2_000)
        val repository = InMemorySessions(planned)
        val evaluate = EvaluateAppLaunchUseCase(
            repository,
            PrivacyPreferencesDataStore(ApplicationProvider.getApplicationContext()),
        )

        assertEquals(AppLaunchEvaluation.ALLOW, evaluate("com.example.calculator"))
        assertEquals(AppLaunchEvaluation.INTERVENE, evaluate("com.example.messages"))
        repository.save(planned.complete(62_000))
        assertEquals(60_000, repository.value.endedAtEpochMillis!! - repository.value.startedAtEpochMillis!!)
        assertEquals(com.hackatudo.conscious.domain.model.FocusSessionStatus.COMPLETED, repository.value.status)
        assertNull(repository.observeCurrent().first())
    }
}

private class InMemorySessions(initial: FocusSession) : FocusSessionRepository {
    private val current = MutableStateFlow<FocusSession?>(initial)
    lateinit var value: FocusSession
    override fun observeCurrent(): Flow<FocusSession?> = current
    override suspend fun get(id: UUID): FocusSession? = current.value?.takeIf { it.id == id }
    override suspend fun save(session: FocusSession) { value = session; current.value = session.takeUnless { it.endedAtEpochMillis != null } }
}
