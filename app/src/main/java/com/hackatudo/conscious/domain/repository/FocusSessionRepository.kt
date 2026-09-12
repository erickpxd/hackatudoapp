package com.hackatudo.conscious.domain.repository

import com.hackatudo.conscious.domain.model.FocusContext
import com.hackatudo.conscious.domain.model.FocusSession
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface FocusSessionRepository {
    fun observeCurrent(): Flow<FocusSession?>
    suspend fun get(id: UUID): FocusSession?
    suspend fun save(session: FocusSession)
}

interface FocusContextRepository {
    fun observeAll(): Flow<List<FocusContext>>
    suspend fun save(context: FocusContext)
    suspend fun delete(context: FocusContext)
}
