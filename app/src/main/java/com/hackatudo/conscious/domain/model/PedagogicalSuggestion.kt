package com.hackatudo.conscious.domain.model

import java.util.UUID

enum class SuggestionStatus { PENDING, ACCEPTED, ADAPTED, IGNORED }

data class PedagogicalSuggestion(
    val id: UUID = UUID.randomUUID(),
    val sourceName: String,
    val title: String,
    val suggestedIntention: String,
    val durationMinutes: Int,
    val relatedPackageNames: Set<String> = emptySet(),
    val status: SuggestionStatus = SuggestionStatus.PENDING,
) {
    init {
        require(sourceName.isNotBlank())
        require(title.isNotBlank())
        require(suggestedIntention.isNotBlank())
        require(durationMinutes > 0)
    }
}
