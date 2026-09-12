package com.hackatudo.conscious.feature.launcher

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.hackatudo.conscious.core.designsystem.component.ErrorState
import com.hackatudo.conscious.core.launcher.HomeRoleStatus

@Composable
fun HomeLauncherScreen(
    state: LauncherUiState,
    onAppClick: (String) -> Unit,
    onRequestHomeRole: () -> Unit,
    onRetry: () -> Unit,
    onNewSession: () -> Unit = {},
) {
    when {
        state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        state.errorMessage != null -> ErrorState(state.errorMessage, onRetry)
        else -> Column(Modifier.fillMaxSize().padding(16.dp)) {
            Text("Sua tela inicial", style = MaterialTheme.typography.headlineSmall)
            state.currentSession?.let { session ->
                Text("Intenção: ${session.currentIntention?.text ?: session.title}")
                Text("Sessão ${session.status.name.lowercase()}")
            } ?: Button(onClick = onNewSession) { Text("Nova sessão") }
            if (state.homeRoleStatus == HomeRoleStatus.NOT_SELECTED) {
                Text("Você pode escolher este app como tela inicial. Essa escolha é reversível.")
                Button(onClick = onRequestHomeRole) { Text("Escolher tela inicial") }
            }
            if (state.apps.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhum aplicativo disponível")
                }
            } else {
                val related = state.currentSession?.selectedPackageNames.orEmpty()
                if (related.isNotEmpty()) Text("Relacionados à sessão")
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(88.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.apps.sortedBy { if (it.packageName in related) 0 else 1 }, key = { it.packageName }) { app ->
                        Column(
                            modifier = Modifier
                                .clickable { onAppClick(app.packageName) }
                                .padding(8.dp)
                                .semantics { contentDescription = "Abrir ${app.displayName}" },
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(app.displayName.take(1).uppercase(), style = MaterialTheme.typography.headlineMedium)
                            Text(app.displayName, style = MaterialTheme.typography.bodySmall)
                            if (related.isNotEmpty() && app.packageName !in related) Text("Fora do contexto", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}
