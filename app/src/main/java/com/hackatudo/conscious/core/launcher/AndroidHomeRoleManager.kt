package com.hackatudo.conscious.core.launcher

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidHomeRoleManager @Inject constructor(
    @ApplicationContext private val context: Context,
) : HomeRoleManager {
    private val mutableStatus = MutableStateFlow(resolveStatus())
    override val status: StateFlow<HomeRoleStatus> = mutableStatus

    override fun refresh() {
        mutableStatus.value = resolveStatus()
    }

    override fun createRequestIntent(): Intent? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return null
        val manager = context.getSystemService(RoleManager::class.java)
        return if (manager.isRoleAvailable(RoleManager.ROLE_HOME) && !manager.isRoleHeld(RoleManager.ROLE_HOME)) {
            manager.createRequestRoleIntent(RoleManager.ROLE_HOME)
        } else null
    }

    private fun resolveStatus(): HomeRoleStatus {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return HomeRoleStatus.UNSUPPORTED
        val manager = context.getSystemService(RoleManager::class.java)
        if (!manager.isRoleAvailable(RoleManager.ROLE_HOME)) return HomeRoleStatus.UNSUPPORTED
        return if (manager.isRoleHeld(RoleManager.ROLE_HOME)) HomeRoleStatus.SELECTED else HomeRoleStatus.NOT_SELECTED
    }
}
