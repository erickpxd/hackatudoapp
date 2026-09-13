package com.hackatudo.conscious.data.repository

import android.content.Context
import com.hackatudo.conscious.core.database.AppDatabase
import com.hackatudo.conscious.data.local.sync.SyncStatus
import com.hackatudo.conscious.data.sync.SyncWorker
import com.hackatudo.conscious.feature.groups.SyncStatusUiState
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SyncRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    @ApplicationContext private val context: Context,
) {
    fun observeStatus(): Flow<SyncStatusUiState> = database.syncOutboxDao().observeAll().map { entries ->
        SyncStatusUiState(
            pending = entries.count { it.status == SyncStatus.PENDING },
            synced = entries.count { it.status == SyncStatus.SYNCED },
            failed = entries.count { it.status == SyncStatus.FAILED },
            requiresAdministrativeDecision = entries.any { it.lastErrorCode == "VERSION_CONFLICT" },
        )
    }

    fun retry() = SyncWorker.enqueue(context)
}
