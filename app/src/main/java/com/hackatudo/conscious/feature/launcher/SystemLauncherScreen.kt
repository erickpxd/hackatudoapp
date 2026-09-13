package com.hackatudo.conscious.feature.launcher

import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.widget.ImageView
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.hackatudo.conscious.core.designsystem.component.ErrorState
import com.hackatudo.conscious.domain.model.InstalledApp
import com.hackatudo.conscious.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay

@Composable
fun SystemLauncherScreen(
    state: LauncherUiState,
    onAppClick: (String) -> Unit,
    onOpenGedu: () -> Unit,
    onRetry: () -> Unit,
) {
    val accent = MaterialTheme.colorScheme.primary
    val wallpaperColor = Color(0xFF111315)
    val ownPackageName = LocalContext.current.packageName
    var query by remember { mutableStateOf("") }
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) { while (true) { delay(30_000); now = System.currentTimeMillis() } }

    when {
        state.isLoading -> Box(Modifier.fillMaxSize().background(Color(0xFF101010)), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        state.errorMessage != null -> ErrorState(state.errorMessage, onRetry)
        else -> {
            val showGedu = "GEDU".contains(query, ignoreCase = true)
            val visibleApps = state.apps.filter { it.packageName != ownPackageName && it.displayName.contains(query, ignoreCase = true) }
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.fillMaxSize().background(wallpaperColor),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 26.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(now)), color = Color.White, fontSize = 58.sp, fontWeight = FontWeight.Light)
                        Text(SimpleDateFormat("EEEE, d 'de' MMMM", Locale("pt", "BR")).format(Date(now)), color = Color.White.copy(alpha = .72f), fontSize = 14.sp)
                        Spacer(Modifier.height(24.dp))
                        OutlinedTextField(
                            value = query,
                            onValueChange = { query = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Buscar aplicativos") },
                            leadingIcon = { Text("⌕", fontSize = 22.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.Black.copy(alpha = .22f),
                                unfocusedContainerColor = Color.Black.copy(alpha = .22f),
                                focusedBorderColor = accent,
                                unfocusedBorderColor = Color.White.copy(alpha = .12f),
                            ),
                        )
                        Spacer(Modifier.height(18.dp))
                    }
                }
                if (showGedu) {
                    item { GeduAppIcon(onOpenGedu) }
                }
                items(visibleApps, key = { it.packageName }) { app ->
                    LauncherAppIcon(app) { onAppClick(app.packageName) }
                }
                if (visibleApps.isEmpty() && !showGedu) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text("Nenhum aplicativo encontrado", color = Color.White.copy(alpha = .65f), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}

@Composable
private fun LauncherAppIcon(app: InstalledApp, onClick: () -> Unit) {
    val context = LocalContext.current
    val accent = MaterialTheme.colorScheme.primary
    val iconTint = accent.toArgb()
    Column(
        modifier = Modifier.clickable(onClick = onClick).semantics { contentDescription = "Abrir ${app.displayName}" },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(Modifier.size(64.dp).background(Color(0xFF1B1E21), RoundedCornerShape(18.dp)), contentAlignment = Alignment.Center) {
            AndroidView(
                factory = {
                    ImageView(it).apply {
                        scaleType = ImageView.ScaleType.FIT_CENTER
                        colorFilter = PorterDuffColorFilter(iconTint, PorterDuff.Mode.SRC_IN)
                        alpha = 0.9f
                    }
                },
                update = {
                    it.setImageDrawable(
                        runCatching { context.packageManager.getApplicationIcon(app.packageName) }
                            .getOrNull()
                            ?.monochromeLayer(),
                    )
                    it.colorFilter = PorterDuffColorFilter(iconTint, PorterDuff.Mode.SRC_IN)
                },
                modifier = Modifier.size(46.dp),
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(app.displayName, color = Color.White.copy(alpha = .72f), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
    }
}

private fun Drawable.monochromeLayer(): Drawable =
    if (this is AdaptiveIconDrawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) monochrome ?: foreground else foreground
    } else {
        this
    }

@Composable
private fun GeduAppIcon(onClick: () -> Unit) {
    val accent = MaterialTheme.colorScheme.primary
    Column(
        modifier = Modifier.clickable(onClick = onClick).semantics { contentDescription = "Abrir GEDU" },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(Modifier.size(64.dp).background(accent.copy(alpha = .24f), RoundedCornerShape(18.dp)), contentAlignment = Alignment.Center) {
            Image(
                painterResource(R.drawable.gedu_logo_icon),
                "GEDU",
                Modifier.size(36.dp),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(accent),
            )
        }
        Spacer(Modifier.height(6.dp))
        Text("GEDU", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}
