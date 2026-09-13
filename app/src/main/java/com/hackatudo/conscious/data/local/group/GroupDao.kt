package com.hackatudo.conscious.data.local.group

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import java.util.UUID
import kotlinx.coroutines.flow.Flow

data class StudyGroupRecord(
    @Embedded val group: StudyGroupEntity,
    @Relation(parentColumn = "id", entityColumn = "groupId") val members: List<GroupMemberEntity>,
    @Relation(parentColumn = "id", entityColumn = "groupId") val mascots: List<MascotEntity>,
    @Relation(parentColumn = "id", entityColumn = "groupId") val goals: List<GroupGoalEntity>,
    @Relation(parentColumn = "id", entityColumn = "groupId") val contributions: List<GroupContributionEntity>,
)

@Dao
interface GroupDao {
    @Transaction
    @Query("SELECT * FROM study_groups ORDER BY createdAtEpochMillis DESC")
    fun observeAll(): Flow<List<StudyGroupRecord>>

    @Transaction
    @Query("SELECT * FROM study_groups WHERE id = :groupId")
    fun observe(groupId: UUID): Flow<StudyGroupRecord?>

    @Transaction
    @Query("SELECT * FROM study_groups WHERE id = :groupId")
    suspend fun get(groupId: UUID): StudyGroupRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertGroup(group: StudyGroupEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertMembers(members: List<GroupMemberEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertMascot(mascot: MascotEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertGoal(goal: GroupGoalEntity)
    @Insert(onConflict = OnConflictStrategy.IGNORE) suspend fun insertContribution(contribution: GroupContributionEntity): Long
    @Query("DELETE FROM study_groups WHERE id = :groupId") suspend fun deleteGroup(groupId: UUID)
}
