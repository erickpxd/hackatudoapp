package com.hackatudo.conscious.app

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hackatudo.conscious.core.launcher.AppLauncher
import com.hackatudo.conscious.core.launcher.HomeRoleManager
import com.hackatudo.conscious.feature.launcher.HomeLauncherScreen
import com.hackatudo.conscious.feature.launcher.LauncherViewModel
import com.hackatudo.conscious.feature.session.active.ActiveSessionScreen
import com.hackatudo.conscious.feature.session.active.ActiveSessionViewModel
import com.hackatudo.conscious.feature.session.create.CreateSessionScreen
import com.hackatudo.conscious.feature.session.create.CreateSessionViewModel
import androidx.compose.runtime.LaunchedEffect
import javax.inject.Inject

class LauncherActions @Inject constructor(
    val appLauncher: AppLauncher,
    val homeRoleManager: HomeRoleManager,
)

@Composable
fun AppNavHost(
    actions: LauncherActions,
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Destination.Launcher.route) {
        composable(Destination.Launcher.route) {
            val viewModel: LauncherViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            val homeRoleLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { viewModel.refreshHomeRole() }
            HomeLauncherScreen(
                state = state,
                onAppClick = { actions.appLauncher.launch(it) },
                onRequestHomeRole = { actions.homeRoleManager.createRequestIntent()?.let(homeRoleLauncher::launch) },
                onRetry = viewModel::retry,
                onNewSession = { navController.navigate(Destination.CreateSession.route) },
            )
        }
        composable(Destination.CreateSession.route) {
            val viewModel: CreateSessionViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            LaunchedEffect(state.started) { if (state.started) navController.navigate(Destination.ActiveSession.route) }
            CreateSessionScreen(state, viewModel::setIntention, viewModel::setDuration, viewModel::toggleApp, viewModel::applyContext, viewModel::start, viewModel::saveContext)
        }
        composable(Destination.ActiveSession.route) {
            val viewModel: ActiveSessionViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            ActiveSessionScreen(state, viewModel::pause, viewModel::resume, { viewModel.complete(); navController.popBackStack(Destination.Launcher.route, false) }, { viewModel.cancel(); navController.popBackStack(Destination.Launcher.route, false) }, viewModel::changeIntention)
        }
    }
}
