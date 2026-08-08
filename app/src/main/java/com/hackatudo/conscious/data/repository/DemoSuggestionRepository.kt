package com.hackatudo.conscious.data.repository

import com.hackatudo.conscious.domain.model.PedagogicalSuggestion
import com.hackatudo.conscious.domain.model.SuggestionStatus
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Singleton
class DemoSuggestionRepository @Inject constructor() {
    private val mutableSuggestion = MutableStateFlow(
        PedagogicalSuggestion(
            sourceName = "Professora Ana • Escola Horizonte",
            title = "Revisão de Matemática",
            suggestedIntention = "Revisar frações para a atividade",
            durationMinutes = 30,
        ),
    )
    val suggestion: StateFlow<PedagogicalSuggestion> = mutableSuggestion

    fun decide(status: SuggestionStatus) {
        require(status != SuggestionStatus.PENDING)
        mutableSuggestion.value = mutableSuggestion.value.copy(status = status)
    }
}
