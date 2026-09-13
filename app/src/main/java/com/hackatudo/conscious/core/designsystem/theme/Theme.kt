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

private fun accentFor(themeColor: String) = when (themeColor) {
    "red" -> Color(0xFFFF334D)
    "blue" -> Color(0xFF0A84FF)
    "green" -> Color(0xFF27AE60)
    "purple" -> Color(0xFF9B51E0)
    "orange" -> Color(0xFFFF8A00)
    "pink" -> Color(0xFFFF4F9A)
    else -> Color(0xFF0A84FF)
}

private fun lightColors(themeColor: String) = lightColorScheme(
    primary = accentFor(themeColor),
    secondary = Color(0xFF52645A),
    background = Color(0xFFF7F9F5),
    surface = Color(0xFFF7F9F5),
)

private fun darkColors(themeColor: String) = darkColorScheme(
    primary = accentFor(themeColor),
    onPrimary = Color.White,
    secondary = Color(0xFFBACBBF),
    background = Color(0xFF101512),
    onBackground = Color.White,
    surface = Color(0xFF101512),
    onSurface = Color.White,
)

@Composable
fun ConsciousUseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeColor: String = "blue",
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) darkColors(themeColor) else lightColors(themeColor),
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
