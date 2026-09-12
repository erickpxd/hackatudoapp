package com.hackatudo.conscious.data.repository

import androidx.room.withTransaction
import com.hackatudo.conscious.core.database.AppDatabase
import com.hackatudo.conscious.data.local.dao.FocusSessionRecord
import com.hackatudo.conscious.data.local.entity.FocusContextEntity
import com.hackatudo.conscious.data.local.entity.FocusSessionEntity
import com.hackatudo.conscious.data.local.entity.SessionAppEntity
import com.hackatudo.conscious.data.local.entity.UsageIntentEntity
import com.hackatudo.conscious.domain.model.FocusContext
import com.hackatudo.conscious.domain.model.FocusSession
import com.hackatudo.conscious.domain.model.UsageIntent
import com.hackatudo.conscious.domain.repository.FocusContextRepository
import com.hackatudo.conscious.domain.repository.FocusSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class RoomFocusSessionRepository @Inject constructor(private val database: AppDatabase) : FocusSessionRepository {
    private val dao get() = database.focusSessionDao()
    override fun observeCurrent(): Flow<FocusSession?> = dao.observeCurrent().map { it?.toDomain() }
    override suspend fun get(id: UUID): FocusSession? = dao.get(id)?.toDomain()
    override suspend fun save(session: FocusSession) = database.withTransaction { dao.replace(session.toRecord()) }
}

class RoomFocusContextRepository @Inject constructor(private val database: AppDatabase) : FocusContextRepository {
    private val dao get() = database.focusContextDao()
    override fun observeAll(): Flow<List<FocusContext>> = dao.observeAll().map { values -> values.map { it.toDomain() } }
    override suspend fun save(context: FocusContext) = dao.upsert(context.toEntity())
    override suspend fun delete(context: FocusContext) = dao.delete(context.toEntity())
}

private fun FocusSession.toRecord() = FocusSessionRecord(
    session = FocusSessionEntity(id, title, plannedDurationMillis, createdAtEpochMillis, status, startedAtEpochMillis, endedAtEpochMillis, accumulatedPauseMillis, pauseStartedAtEpochMillis, groupId),
    intentions = intentions.map { UsageIntentEntity(it.id, it.sessionId, it.text, it.source, it.createdAtEpochMillis, it.activeUntilEpochMillis) },
    apps = selectedPackageNames.map { SessionAppEntity(id, it) },
)

private fun FocusSessionRecord.toDomain() = FocusSession(
    id = session.id, title = session.title, plannedDurationMillis = session.plannedDurationMillis,
    createdAtEpochMillis = session.createdAtEpochMillis, status = session.status,
    startedAtEpochMillis = session.startedAtEpochMillis, endedAtEpochMillis = session.endedAtEpochMillis,
    accumulatedPauseMillis = session.accumulatedPauseMillis, pauseStartedAtEpochMillis = session.pauseStartedAtEpochMillis,
    intentions = intentions.sortedBy { it.createdAtEpochMillis }.map { UsageIntent(it.id, it.sessionId, it.text, it.source, it.createdAtEpochMillis, it.activeUntilEpochMillis) },
    selectedPackageNames = apps.mapTo(mutableSetOf()) { it.packageName }, groupId = session.groupId,
)

private fun FocusContext.toEntity() = FocusContextEntity(id, name, suggestedIntention, relatedPackageNames.toList(), source)
private fun FocusContextEntity.toDomain() = FocusContext(id, name, suggestedIntention, relatedPackageNames.toSet(), source)
