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

@Database(
    entities = [StudentProfileEntity::class, FocusSessionEntity::class, UsageIntentEntity::class, SessionAppEntity::class, FocusContextEntity::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(DatabaseConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun focusSessionDao(): FocusSessionDao
    abstract fun focusContextDao(): FocusContextDao
}
