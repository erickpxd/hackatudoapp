package com.hackatudo.conscious.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hackatudo.conscious.core.database.AppDatabase
import com.hackatudo.conscious.data.local.dao.FocusSessionRecord
import com.hackatudo.conscious.data.local.entity.FocusSessionEntity
import com.hackatudo.conscious.data.local.entity.SessionAppEntity
import com.hackatudo.conscious.data.local.entity.UsageIntentEntity
import com.hackatudo.conscious.domain.model.ContentSource
import com.hackatudo.conscious.domain.model.FocusSessionStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class FocusSessionDaoTest {
    @Test fun persistsAssociationAndRestoresCurrentSession() = runTest {
        val database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java).build()
        val id = UUID.randomUUID()
        database.focusSessionDao().replace(FocusSessionRecord(
            FocusSessionEntity(id, "Matemática", 1_000, 1, FocusSessionStatus.ACTIVE, 1, null, 0, null, null),
            listOf(UsageIntentEntity(UUID.randomUUID(), id, "Matemática", ContentSource.STUDENT, 1, null)),
            listOf(SessionAppEntity(id, "calculator")),
        ))
        assertEquals("calculator", database.focusSessionDao().observeCurrent().first()!!.apps.single().packageName)
        database.close()
    }
}
