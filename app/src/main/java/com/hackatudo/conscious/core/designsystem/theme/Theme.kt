package com.hackatudo.conscious.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

private val LightColors = lightColorScheme(
    primary = Color(0xFF3F6654),
    secondary = Color(0xFF52645A),
    background = Color(0xFFF7F9F5),
    surface = Color(0xFFF7F9F5),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFA5CFB5),
    secondary = Color(0xFFBACBBF),
    background = Color(0xFF101512),
    surface = Color(0xFF101512),
)

@Composable
fun ConsciousUseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = MaterialTheme.typography,
        content = content,
    )
}

@Preview(name = "Tema claro", showBackground = true)
@Composable
private fun LightThemePreview() {
    ConsciousUseTheme(darkTheme = false) {
        Surface { Text("Uso consciente") }
    }
}

@Preview(name = "Tema escuro", showBackground = true)
@Composable
private fun DarkThemePreview() {
    ConsciousUseTheme(darkTheme = true) {
        Surface { Text("Uso consciente") }
    }
}
