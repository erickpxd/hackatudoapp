package com.hackatudo.conscious.core.database

import androidx.room.TypeConverter
import java.util.UUID
import com.hackatudo.conscious.domain.model.ContentSource
import com.hackatudo.conscious.domain.model.FocusSessionStatus

class DatabaseConverters {
    @TypeConverter fun uuidToString(value: UUID?): String? = value?.toString()
    @TypeConverter fun stringToUuid(value: String?): UUID? = value?.let(UUID::fromString)
    @TypeConverter fun stringsToText(value: List<String>): String = value.joinToString("\n")
    @TypeConverter fun textToStrings(value: String): List<String> = if (value.isBlank()) emptyList() else value.split("\n")
    @TypeConverter fun statusToText(value: FocusSessionStatus): String = value.name
    @TypeConverter fun textToStatus(value: String): FocusSessionStatus = FocusSessionStatus.valueOf(value)
    @TypeConverter fun sourceToText(value: ContentSource): String = value.name
    @TypeConverter fun textToSource(value: String): ContentSource = ContentSource.valueOf(value)
}
