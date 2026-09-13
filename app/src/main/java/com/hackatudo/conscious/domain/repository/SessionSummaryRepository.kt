package com.hackatudo.conscious.domain.repository

import com.hackatudo.conscious.domain.model.SessionSummarySource
import java.util.UUID
import kotlinx.coroutines.flow.Flow

interface SessionSummaryRepository {
    suspend fun getSource(sessionId: UUID): SessionSummarySource?
    fun observeSources(): Flow<List<SessionSummarySource>>
}
