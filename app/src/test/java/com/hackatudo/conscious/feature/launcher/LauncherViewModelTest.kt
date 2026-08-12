package com.hackatudo.conscious.feature.launcher

import com.hackatudo.conscious.TestDispatcherRule
import com.hackatudo.conscious.core.launcher.HomeRoleManager
import com.hackatudo.conscious.core.launcher.HomeRoleStatus
import com.hackatudo.conscious.core.datastore.PrivacyPreferences
import com.hackatudo.conscious.core.datastore.PrivacyPreferencesDataStore
import com.hackatudo.conscious.domain.model.InstalledApp
import com.hackatudo.conscious.domain.model.PersonalInsights
import com.hackatudo.conscious.domain.repository.InstalledAppsRepository
import com.hackatudo.conscious.domain.usecase.session.GetCurrentSessionUseCase
import com.hackatudo.conscious.domain.usecase.intervention.EvaluateAppLaunchUseCase
import com.hackatudo.conscious.domain.usecase.insights.GetPersonalInsightsUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class LauncherViewModelTest {
    @get:Rule val dispatcherRule = TestDispatcherRule()

    @Test
    fun `exposes loaded apps and home role`() = runTest(dispatcherRule.dispatcher) {
        val repository = mockk<InstalledAppsRepository>()
        val homeRole = mockk<HomeRoleManager>()
        val currentSession = mockk<GetCurrentSessionUseCase>()
        val evaluateAppLaunch = mockk<EvaluateAppLaunchUseCase>()
        val insights = mockk<GetPersonalInsightsUseCase>()
        val preferences = mockk<PrivacyPreferencesDataStore>()
        every { repository.observeLaunchableApps() } returns flowOf(
            listOf(InstalledApp("calculator", "Calculadora", "calculator", true, false)),
        )
        every { homeRole.status } returns MutableStateFlow(HomeRoleStatus.NOT_SELECTED)
        every { currentSession() } returns flowOf(null)
        every { insights() } returns flowOf(PersonalInsights())
        every { preferences.preferences } returns flowOf(PrivacyPreferences(userName = "Rob", petName = "neko"))

        val viewModel = LauncherViewModel(repository, homeRole, currentSession, evaluateAppLaunch, insights, preferences)
        advanceUntilIdle()

        assertEquals("Calculadora", viewModel.uiState.value.apps.single().displayName)
        assertEquals(HomeRoleStatus.NOT_SELECTED, viewModel.uiState.value.homeRoleStatus)
    }
}
