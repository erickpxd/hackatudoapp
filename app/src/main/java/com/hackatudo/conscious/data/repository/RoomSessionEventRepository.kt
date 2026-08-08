package com.hackatudo.conscious.data.repository

import com.hackatudo.conscious.core.database.AppDatabase
import com.hackatudo.conscious.data.local.entity.SessionEventEntity
import com.hackatudo.conscious.domain.model.SessionEvent
import com.hackatudo.conscious.domain.repository.SessionEventRepository
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomSessionEventRepository @Inject constructor(
    database: AppDatabase,
) : SessionEventRepository {
    private val dao = database.sessionEventDao()

    override fun observeForSession(sessionId: UUID): Flow<List<SessionEvent>> =
        dao.observeForSession(sessionId).map { events -> events.map(SessionEventEntity::toDomain) }

    override suspend fun getForSession(sessionId: UUID): List<SessionEvent> =
        dao.getForSession(sessionId).map(SessionEventEntity::toDomain)

    override suspend fun record(event: SessionEvent) = dao.insert(event.toEntity())
}

private fun SessionEvent.toEntity() = SessionEventEntity(
    id = id,
    sessionId = sessionId,
    type = type,
    occurredAtEpochMillis = occurredAtEpochMillis,
    targetPackage = targetPackage,
    decision = decision,
    reflectionReason = reflectionReason,
    intentId = intentId,
)

private fun SessionEventEntity.toDomain() = SessionEvent(
    id = id,
    sessionId = sessionId,
    type = type,
    occurredAtEpochMillis = occurredAtEpochMillis,
    targetPackage = targetPackage,
    decision = decision,
    reflectionReason = reflectionReason,
    intentId = intentId,
)
