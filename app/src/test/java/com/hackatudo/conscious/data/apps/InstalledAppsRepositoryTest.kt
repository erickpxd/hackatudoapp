package com.hackatudo.conscious.data.apps

import com.hackatudo.conscious.domain.model.InstalledApp
import org.junit.Assert.assertEquals
import org.junit.Test

class InstalledAppsRepositoryTest {
    @Test
    fun `sorts launchable apps by display name and removes invalid entries`() {
        val apps = listOf(
            InstalledApp("z", "Zeta", "z", launchable = true, systemApp = false),
            InstalledApp("invalid", "Inválido", "invalid", launchable = false, systemApp = false),
            InstalledApp("a", "Ábaco", "a", launchable = true, systemApp = false),
        )

        assertEquals(listOf("a", "z"), apps.normalizedLaunchableApps().map { it.packageName })
    }
}
