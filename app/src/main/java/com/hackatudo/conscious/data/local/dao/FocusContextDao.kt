package com.hackatudo.conscious.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.hackatudo.conscious.data.local.entity.FocusContextEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusContextDao {
    @Query("SELECT * FROM focus_contexts ORDER BY name") fun observeAll(): Flow<List<FocusContextEntity>>
    @Upsert suspend fun upsert(entity: FocusContextEntity)
    @Delete suspend fun delete(entity: FocusContextEntity)
}
