package com.hackatudo.conscious.data.local.group

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.hackatudo.conscious.domain.model.group.ContributionKind
import com.hackatudo.conscious.domain.model.group.GoalMetric
import com.hackatudo.conscious.domain.model.group.GroupGoalStatus
import com.hackatudo.conscious.domain.model.group.GroupMemberStatus
import com.hackatudo.conscious.domain.model.group.GroupRole
import com.hackatudo.conscious.domain.model.group.MascotStage
import java.util.UUID

@Entity(tableName = "study_groups")
data class StudyGroupEntity(
    @PrimaryKey val id: UUID,
    val name: String,
    val description: String,
    val objective: String,
    val ownerMembershipId: UUID,
    val createdAtEpochMillis: Long,
)

@Entity(
    tableName = "group_members",
    foreignKeys = [ForeignKey(entity = StudyGroupEntity::class, parentColumns = ["id"], childColumns = ["groupId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("groupId"), Index(value = ["groupId", "participantAlias"], unique = true)],
)
data class GroupMemberEntity(
    @PrimaryKey val id: UUID,
    val groupId: UUID,
    val participantAlias: String,
    val role: GroupRole,
    val status: GroupMemberStatus,
    val joinedAtEpochMillis: Long,
)

@Entity(
    tableName = "mascots",
    foreignKeys = [ForeignKey(entity = StudyGroupEntity::class, parentColumns = ["id"], childColumns = ["groupId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index(value = ["groupId"], unique = true)],
)
data class MascotEntity(
    @PrimaryKey val id: UUID,
    val groupId: UUID,
    val type: String,
    val stage: MascotStage,
    val progressPoints: Long,
)

@Entity(
    tableName = "group_goals",
    foreignKeys = [ForeignKey(entity = StudyGroupEntity::class, parentColumns = ["id"], childColumns = ["groupId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("groupId")],
)
data class GroupGoalEntity(
    @PrimaryKey val id: UUID,
    val groupId: UUID,
    val title: String,
    val metric: GoalMetric,
    val target: Long,
    val currentValue: Long,
    val startsAtEpochMillis: Long,
    val endsAtEpochMillis: Long,
    val status: GroupGoalStatus,
)

@Entity(
    tableName = "group_contributions",
    foreignKeys = [ForeignKey(entity = StudyGroupEntity::class, parentColumns = ["id"], childColumns = ["groupId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("groupId"), Index("goalId")],
)
data class GroupContributionEntity(
    @PrimaryKey val id: UUID,
    val groupId: UUID,
    val goalId: UUID?,
    val kind: ContributionKind,
    val amount: Long,
    val occurredAtEpochMillis: Long,
)
