package com.hackatudo.conscious.feature.insights

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun PersonalInsightsScreen(state: PersonalInsightsUiState) {
    if (state.isLoading) {
        CircularProgressIndicator()
        return
    }
    val insights = state.insights
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            "Seus indicadores",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.semantics { heading() },
        )
        Text("Sessões registradas: ${insights.sessionCount}")
        Text("Tempo total: ${insights.totalDurationMillis / 60_000} min")
        Text("Duração média: ${insights.averageDurationMillis / 60_000} min")
        Text("Dias com sessões: ${insights.activeDayCount}")
        Text("Consistência recente: ${insights.currentConsistencyDays} dia(s)")
        Text("Estes dados permanecem neste aparelho e mudam quando você exclui seu histórico.")
    }
}
