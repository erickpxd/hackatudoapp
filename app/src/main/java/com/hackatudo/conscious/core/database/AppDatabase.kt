package com.hackatudo.conscious.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hackatudo.conscious.data.local.entity.StudentProfileEntity
import com.hackatudo.conscious.data.local.dao.FocusContextDao
import com.hackatudo.conscious.data.local.dao.FocusSessionDao
import com.hackatudo.conscious.data.local.entity.FocusContextEntity
import com.hackatudo.conscious.data.local.entity.FocusSessionEntity
import com.hackatudo.conscious.data.local.entity.SessionAppEntity
import com.hackatudo.conscious.data.local.entity.UsageIntentEntity
import com.hackatudo.conscious.data.local.dao.SessionEventDao
import com.hackatudo.conscious.data.local.dao.SessionSummaryDao
import com.hackatudo.conscious.data.local.entity.SessionEventEntity
import com.hackatudo.conscious.data.local.group.GroupContributionEntity
import com.hackatudo.conscious.data.local.group.GroupDao
import com.hackatudo.conscious.data.local.group.GroupGoalEntity
import com.hackatudo.conscious.data.local.group.GroupMemberEntity
import com.hackatudo.conscious.data.local.group.MascotEntity
import com.hackatudo.conscious.data.local.group.StudyGroupEntity
import com.hackatudo.conscious.data.local.sync.SyncOutboxDao
import com.hackatudo.conscious.data.local.sync.SyncOutboxEntity

@Database(
    entities = [StudentProfileEntity::class, FocusSessionEntity::class, UsageIntentEntity::class, SessionAppEntity::class, FocusContextEntity::class, SessionEventEntity::class, StudyGroupEntity::class, GroupMemberEntity::class, MascotEntity::class, GroupGoalEntity::class, GroupContributionEntity::class, SyncOutboxEntity::class],
    version = 4,
    autoMigrations = [androidx.room.AutoMigration(from = 1, to = 2), androidx.room.AutoMigration(from = 2, to = 3), androidx.room.AutoMigration(from = 3, to = 4)],
    exportSchema = true,
)
@TypeConverters(DatabaseConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun focusSessionDao(): FocusSessionDao
    abstract fun focusContextDao(): FocusContextDao
    abstract fun sessionEventDao(): SessionEventDao
    abstract fun sessionSummaryDao(): SessionSummaryDao
    abstract fun groupDao(): GroupDao
    abstract fun syncOutboxDao(): SyncOutboxDao
}
