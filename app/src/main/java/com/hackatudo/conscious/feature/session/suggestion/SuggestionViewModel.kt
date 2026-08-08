package com.hackatudo.conscious.feature.session.suggestion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hackatudo.conscious.data.repository.DemoSuggestionRepository
import com.hackatudo.conscious.domain.model.ContentSource
import com.hackatudo.conscious.domain.model.FocusContext
import com.hackatudo.conscious.domain.model.PedagogicalSuggestion
import com.hackatudo.conscious.domain.model.SuggestionStatus
import com.hackatudo.conscious.domain.repository.FocusContextRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface SuggestionEffect { data class OpenEditableSession(val adapt: Boolean) : SuggestionEffect; data object Close : SuggestionEffect }

@HiltViewModel
class SuggestionViewModel @Inject constructor(
    private val repository: DemoSuggestionRepository,
    private val contexts: FocusContextRepository,
) : ViewModel() {
    val suggestion: StateFlow<PedagogicalSuggestion> = repository.suggestion
    private val mutableEffects = MutableSharedFlow<SuggestionEffect>()
    val effects: SharedFlow<SuggestionEffect> = mutableEffects

    fun accept() = open(adapt = false)
    fun adapt() = open(adapt = true)
    fun ignore() {
        repository.decide(SuggestionStatus.IGNORED)
        viewModelScope.launch { mutableEffects.emit(SuggestionEffect.Close) }
    }

    private fun open(adapt: Boolean) = viewModelScope.launch {
        val item = suggestion.value
        repository.decide(if (adapt) SuggestionStatus.ADAPTED else SuggestionStatus.ACCEPTED)
        contexts.save(FocusContext(name = item.title, suggestedIntention = item.suggestedIntention, relatedPackageNames = item.relatedPackageNames, source = ContentSource.TEACHER))
        mutableEffects.emit(SuggestionEffect.OpenEditableSession(adapt))
    }
}
