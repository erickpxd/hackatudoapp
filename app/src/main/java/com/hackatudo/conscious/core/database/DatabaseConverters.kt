package com.hackatudo.conscious.core.database

import androidx.room.TypeConverter
import java.util.UUID
import com.hackatudo.conscious.domain.model.ContentSource
import com.hackatudo.conscious.domain.model.FocusSessionStatus
import com.hackatudo.conscious.domain.model.InterventionDecision
import com.hackatudo.conscious.domain.model.ReflectionReason
import com.hackatudo.conscious.domain.model.SessionEventType
import com.hackatudo.conscious.domain.model.group.ContributionKind
import com.hackatudo.conscious.domain.model.group.GoalMetric
import com.hackatudo.conscious.domain.model.group.GroupGoalStatus
import com.hackatudo.conscious.domain.model.group.GroupMemberStatus
import com.hackatudo.conscious.domain.model.group.GroupRole
import com.hackatudo.conscious.domain.model.group.MascotStage
import com.hackatudo.conscious.data.local.sync.SyncStatus

class DatabaseConverters {
    @TypeConverter fun uuidToString(value: UUID?): String? = value?.toString()
    @TypeConverter fun stringToUuid(value: String?): UUID? = value?.let(UUID::fromString)
    @TypeConverter fun stringsToText(value: List<String>): String = value.joinToString("\n")
    @TypeConverter fun textToStrings(value: String): List<String> = if (value.isBlank()) emptyList() else value.split("\n")
    @TypeConverter fun statusToText(value: FocusSessionStatus): String = value.name
    @TypeConverter fun textToStatus(value: String): FocusSessionStatus = FocusSessionStatus.valueOf(value)
    @TypeConverter fun sourceToText(value: ContentSource): String = value.name
    @TypeConverter fun textToSource(value: String): ContentSource = ContentSource.valueOf(value)
    @TypeConverter fun eventTypeToText(value: SessionEventType?): String? = value?.name
    @TypeConverter fun textToEventType(value: String?): SessionEventType? = value?.let(SessionEventType::valueOf)
    @TypeConverter fun decisionToText(value: InterventionDecision?): String? = value?.name
    @TypeConverter fun textToDecision(value: String?): InterventionDecision? = value?.let(InterventionDecision::valueOf)
    @TypeConverter fun reasonToText(value: ReflectionReason?): String? = value?.name
    @TypeConverter fun textToReason(value: String?): ReflectionReason? = value?.let(ReflectionReason::valueOf)
    @TypeConverter fun groupRoleToText(value: GroupRole): String = value.name
    @TypeConverter fun textToGroupRole(value: String): GroupRole = GroupRole.valueOf(value)
    @TypeConverter fun memberStatusToText(value: GroupMemberStatus): String = value.name
    @TypeConverter fun textToMemberStatus(value: String): GroupMemberStatus = GroupMemberStatus.valueOf(value)
    @TypeConverter fun mascotStageToText(value: MascotStage): String = value.name
    @TypeConverter fun textToMascotStage(value: String): MascotStage = MascotStage.valueOf(value)
    @TypeConverter fun goalMetricToText(value: GoalMetric): String = value.name
    @TypeConverter fun textToGoalMetric(value: String): GoalMetric = GoalMetric.valueOf(value)
    @TypeConverter fun goalStatusToText(value: GroupGoalStatus): String = value.name
    @TypeConverter fun textToGoalStatus(value: String): GroupGoalStatus = GroupGoalStatus.valueOf(value)
    @TypeConverter fun contributionKindToText(value: ContributionKind): String = value.name
    @TypeConverter fun textToContributionKind(value: String): ContributionKind = ContributionKind.valueOf(value)
    @TypeConverter fun syncStatusToText(value: SyncStatus): String = value.name
    @TypeConverter fun textToSyncStatus(value: String): SyncStatus = SyncStatus.valueOf(value)
}
