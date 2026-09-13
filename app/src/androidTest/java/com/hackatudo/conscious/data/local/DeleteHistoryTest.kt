package com.hackatudo.conscious.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hackatudo.conscious.core.database.AppDatabase
import com.hackatudo.conscious.data.local.dao.FocusSessionRecord
import com.hackatudo.conscious.data.local.entity.FocusSessionEntity
import com.hackatudo.conscious.data.local.entity.SessionEventEntity
import com.hackatudo.conscious.data.repository.RoomFocusSessionRepository
import com.hackatudo.conscious.domain.model.FocusSessionStatus
import com.hackatudo.conscious.domain.model.SessionEventType
import java.util.UUID
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DeleteHistoryTest {
    @Test
    fun deletesSessionDependentsAndAllHistoryWithoutDeletingProfileData() = runTest {
        val database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        ).build()
        val firstId = insertCompleted(database, 1)
        val secondId = insertCompleted(database, 2)
        database.openHelper.writableDatabase.execSQL(
            "INSERT INTO student_profiles (id, displayName, createdAtEpochMillis) VALUES (?, ?, ?)",
            arrayOf(UUID.randomUUID().toString(), "Estudante", 1L),
        )
        database.sessionEventDao().insert(
            SessionEventEntity(
                UUID.randomUUID(),
                firstId,
                SessionEventType.INTERVENTION_SHOWN,
                5,
                "chat",
                null,
                null,
                null,
            ),
        )
        val repository = RoomFocusSessionRepository(database)

        repository.delete(firstId)

        assertNull(database.focusSessionDao().get(firstId))
        assertEquals(0, database.sessionEventDao().getForSession(firstId).size)
        assertNotNull(database.focusSessionDao().get(secondId))

        repository.deleteAll()

        assertNull(database.focusSessionDao().get(secondId))
        database.openHelper.readableDatabase.query("SELECT COUNT(*) FROM student_profiles").use {
            it.moveToFirst()
            assertEquals(1, it.getInt(0))
        }
        database.close()
    }

    private suspend fun insertCompleted(database: AppDatabase, timestamp: Long): UUID {
        val id = UUID.randomUUID()
        database.focusSessionDao().replace(
            FocusSessionRecord(
                FocusSessionEntity(
                    id,
                    "Sessão $timestamp",
                    1_000,
                    timestamp,
                    FocusSessionStatus.COMPLETED,
                    timestamp,
                    timestamp + 500,
                    0,
                    null,
                    null,
                ),
                emptyList(),
                emptyList(),
            ),
        )
        return id
    }
}
