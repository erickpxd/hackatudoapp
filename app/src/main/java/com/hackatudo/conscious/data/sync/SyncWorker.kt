package com.hackatudo.conscious.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.hackatudo.conscious.core.database.AppDatabase
import com.hackatudo.conscious.core.network.SharedApi
import com.hackatudo.conscious.core.network.SharedSessionSummaryDto
import com.hackatudo.conscious.data.local.sync.SyncStatus
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.Instant

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted parameters: WorkerParameters,
    private val database: AppDatabase,
    private val api: SharedApi,
) : CoroutineWorker(context, parameters) {
    override suspend fun doWork(): Result {
        var retryableFailure = false
        database.syncOutboxDao().pending().forEach { entry ->
            try {
                val response = api.sendSummary(
                    idempotencyKey = entry.id.toString(),
                    request = SharedSessionSummaryDto(
                        entry.id,
                        entry.groupId,
                        entry.durationMinutes,
                        entry.completed,
                        Instant.ofEpochMilli(entry.occurredAtEpochMillis).toString(),
                    ),
                )
                when {
                    response.isSuccessful -> database.syncOutboxDao().updateStatus(entry.id, SyncStatus.SYNCED)
                    response.code() == 409 -> database.syncOutboxDao().updateStatus(entry.id, SyncStatus.FAILED, "VERSION_CONFLICT")
                    response.code() in 500..599 -> retryableFailure = true
                    else -> database.syncOutboxDao().updateStatus(entry.id, SyncStatus.FAILED, "HTTP_${response.code()}")
                }
            } catch (_: java.io.IOException) {
                retryableFailure = true
            }
        }
        return if (retryableFailure) Result.retry() else Result.success()
    }

    companion object {
        private const val UNIQUE_WORK = "shared-summary-sync"
        fun enqueue(context: Context) {
            val request = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .setBackoffCriteria(androidx.work.BackoffPolicy.EXPONENTIAL, java.time.Duration.ofSeconds(30))
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(UNIQUE_WORK, ExistingWorkPolicy.KEEP, request)
        }
    }
}
