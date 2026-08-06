package com.hackatudo.conscious.core.launcher

import android.content.Intent
import com.hackatudo.conscious.core.common.AppResult
import kotlinx.coroutines.flow.StateFlow

interface AppLauncher {
    fun launch(packageName: String): AppResult<Unit>
}

enum class HomeRoleStatus { UNSUPPORTED, NOT_SELECTED, SELECTED }

interface HomeRoleManager {
    val status: StateFlow<HomeRoleStatus>
    fun refresh()
    fun createRequestIntent(): Intent?
}
