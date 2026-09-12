package com.hackatudo.conscious.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import com.hackatudo.conscious.core.designsystem.theme.ConsciousUseTheme

@Composable
fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(message)
        Button(onClick = onRetry) { Text("Tentar novamente") }
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorStatePreview() {
    ConsciousUseTheme { ErrorState("Não foi possível carregar.", onRetry = {}) }
}
