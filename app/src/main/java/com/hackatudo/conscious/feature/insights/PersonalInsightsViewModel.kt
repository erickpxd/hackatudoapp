package com.hackatudo.conscious.feature.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hackatudo.conscious.domain.model.PersonalInsights
import com.hackatudo.conscious.domain.usecase.insights.GetPersonalInsightsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class PersonalInsightsUiState(
    val isLoading: Boolean = true,
    val insights: PersonalInsights = PersonalInsights(),
)

@HiltViewModel
class PersonalInsightsViewModel @Inject constructor(
    insights: GetPersonalInsightsUseCase,
) : ViewModel() {
    val uiState: StateFlow<PersonalInsightsUiState> = insights()
        .map { PersonalInsightsUiState(isLoading = false, insights = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PersonalInsightsUiState())
}
