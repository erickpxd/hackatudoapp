package com.hackatudo.conscious.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.hackatudo.conscious.core.designsystem.theme.ConsciousUseTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var launcherActions: LauncherActions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ConsciousUseTheme { AppNavHost(actions = launcherActions) } }
    }
}
