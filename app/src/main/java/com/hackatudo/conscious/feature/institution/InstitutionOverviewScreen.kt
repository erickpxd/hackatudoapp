package com.hackatudo.conscious.feature.institution

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hackatudo.conscious.data.demo.InstitutionalAggregate

@Composable
fun InstitutionOverviewScreen(aggregate: InstitutionalAggregate) {
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Visão geral da turma")
        Text(aggregate.classroomName)
        Text("Sessões: ${aggregate.sessionCount}")
        Text("Duração média: ${aggregate.averageDurationMinutes} min")
        Text("Conclusão: ${aggregate.completionRatePercent}%")
        Text("Intervenções: ${aggregate.interventionCount}")
        Text("Tendência: ${aggregate.trend}")
        Text("Estes totais não permitem localizar ou abrir dados de estudantes.")
    }
}
