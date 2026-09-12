package com.hackatudo.conscious.feature.session.selectapps

import androidx.compose.runtime.Composable
import com.hackatudo.conscious.feature.session.create.CreateSessionScreen
import com.hackatudo.conscious.feature.session.create.CreateSessionUiState

@Composable
fun SelectAppsScreen(state: CreateSessionUiState, onToggleApp: (String) -> Unit) {
    CreateSessionScreen(state, {}, {}, onToggleApp, {}, {})
}
