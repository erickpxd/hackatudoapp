package com.hackatudo.conscious.domain.repository

import com.hackatudo.conscious.domain.model.SessionEvent
import java.util.UUID
import kotlinx.coroutines.flow.Flow

interface SessionEventRepository {
    fun observeForSession(sessionId: UUID): Flow<List<SessionEvent>>
    suspend fun getForSession(sessionId: UUID): List<SessionEvent>
    suspend fun record(event: SessionEvent)
}
