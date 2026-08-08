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
import com.hackatudo.conscious.feature.launcher.LauncherEffect
import com.hackatudo.conscious.feature.intervention.InterventionEffect
import com.hackatudo.conscious.feature.intervention.InterventionScreen
import com.hackatudo.conscious.feature.intervention.InterventionViewModel
import com.hackatudo.conscious.feature.session.active.ActiveSessionScreen
import com.hackatudo.conscious.feature.session.active.ActiveSessionViewModel
import com.hackatudo.conscious.feature.session.active.ActiveSessionEffect
import com.hackatudo.conscious.feature.summary.SessionSummaryEffect
import com.hackatudo.conscious.feature.summary.SessionSummaryScreen
import com.hackatudo.conscious.feature.summary.SessionSummaryViewModel
import com.hackatudo.conscious.feature.insights.PersonalInsightsScreen
import com.hackatudo.conscious.feature.insights.PersonalInsightsViewModel
import com.hackatudo.conscious.feature.groups.CreateGroupScreen
import com.hackatudo.conscious.feature.groups.CreateGroupViewModel
import com.hackatudo.conscious.feature.groups.GroupsEffect
import com.hackatudo.conscious.feature.groups.GroupsScreen
import com.hackatudo.conscious.feature.groups.GroupsViewModel
import com.hackatudo.conscious.feature.groups.detail.GroupDetailsScreen
import com.hackatudo.conscious.feature.groups.detail.GroupDetailsViewModel
import com.hackatudo.conscious.feature.session.create.CreateSessionScreen
import com.hackatudo.conscious.feature.session.create.CreateSessionViewModel
import com.hackatudo.conscious.feature.onboarding.OnboardingScreen
import com.hackatudo.conscious.feature.onboarding.OnboardingViewModel
import com.hackatudo.conscious.feature.session.suggestion.SuggestionEffect
import com.hackatudo.conscious.feature.session.suggestion.SuggestionScreen
import com.hackatudo.conscious.feature.session.suggestion.SuggestionViewModel
import com.hackatudo.conscious.feature.institution.InstitutionOverviewScreen
import com.hackatudo.conscious.data.demo.InstitutionalAggregateFixtures
import com.hackatudo.conscious.feature.settings.SettingsScreen
import com.hackatudo.conscious.feature.settings.SettingsViewModel
import com.hackatudo.conscious.feature.settings.UsageAccessSettingsScreen
import com.hackatudo.conscious.data.apps.AndroidUsageStatsRepository
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavType
import androidx.navigation.navArgument
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
    NavHost(navController = navController, startDestination = Destination.Onboarding.route) {
        composable(Destination.Onboarding.route) {
            val viewModel: OnboardingViewModel = hiltViewModel()
            LaunchedEffect(viewModel) {
                viewModel.completed.collect {
                    navController.navigate(Destination.Launcher.route) { popUpTo(Destination.Onboarding.route) { inclusive = true } }
                }
            }
            OnboardingScreen(viewModel::finish)
        }
        composable(Destination.Launcher.route) {
            val viewModel: LauncherViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            val homeRoleLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { viewModel.refreshHomeRole() }
            LaunchedEffect(viewModel) {
                viewModel.effects.collect { effect ->
                    when (effect) {
                        is LauncherEffect.OpenApp -> actions.appLauncher.launch(effect.packageName)
                        is LauncherEffect.ShowIntervention -> navController.navigate(Destination.Intervention.createRoute(effect.packageName))
                    }
                }
            }
            HomeLauncherScreen(
                state = state,
                onAppClick = viewModel::requestAppLaunch,
                onRequestHomeRole = { actions.homeRoleManager.createRequestIntent()?.let(homeRoleLauncher::launch) },
                onRetry = viewModel::retry,
                onNewSession = { navController.navigate(Destination.CreateSession.route) },
                onInsights = { navController.navigate(Destination.PersonalInsights.route) },
                onGroups = { navController.navigate(Destination.Groups.route) },
                onSuggestion = { navController.navigate(Destination.Suggestion.route) },
                onInstitution = { navController.navigate(Destination.Institution.route) },
                onSettings = { navController.navigate(Destination.Settings.route) },
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
            LaunchedEffect(viewModel) {
                viewModel.effects.collect { effect ->
                    when (effect) {
                        is ActiveSessionEffect.ShowSummary -> navController.navigate(Destination.SessionSummary.createRoute(effect.sessionId)) {
                            popUpTo(Destination.Launcher.route)
                        }
                        ActiveSessionEffect.ReturnToLauncher -> navController.popBackStack(Destination.Launcher.route, false)
                    }
                }
            }
            ActiveSessionScreen(state, viewModel::pause, viewModel::resume, viewModel::complete, viewModel::cancel, viewModel::changeIntention)
        }
        composable(
            route = Destination.Intervention.route,
            arguments = listOf(navArgument(Destination.Intervention.packageNameArgument) { type = NavType.StringType }),
        ) {
            val viewModel: InterventionViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            LaunchedEffect(viewModel) {
                viewModel.effects.collect { effect ->
                    when (effect) {
                        InterventionEffect.ReturnToLauncher -> navController.popBackStack(Destination.Launcher.route, false)
                        is InterventionEffect.OpenApp -> {
                            actions.appLauncher.launch(effect.packageName)
                            navController.popBackStack(Destination.Launcher.route, false)
                        }
                    }
                }
            }
            InterventionScreen(
                state = state,
                onReasonSelected = viewModel::selectReason,
                onStayFocused = viewModel::stayFocused,
                onOpenAnyway = viewModel::openAnyway,
                onChangeIntention = { navController.navigate(Destination.ActiveSession.route) },
                onEndSession = { navController.navigate(Destination.ActiveSession.route) },
            )
        }
        composable(
            route = Destination.SessionSummary.route,
            arguments = listOf(navArgument(Destination.SessionSummary.sessionIdArgument) { type = NavType.StringType }),
        ) {
            val viewModel: SessionSummaryViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            LaunchedEffect(viewModel) {
                viewModel.effects.collect {
                    navController.popBackStack(Destination.Launcher.route, false)
                }
            }
            SessionSummaryScreen(
                state = state,
                onRetry = viewModel::retry,
                onDone = { navController.popBackStack(Destination.Launcher.route, false) },
                onDeleteSession = viewModel::deleteSession,
                onDeleteAllHistory = viewModel::deleteAllHistory,
            )
        }
        composable(Destination.PersonalInsights.route) {
            val viewModel: PersonalInsightsViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            PersonalInsightsScreen(state)
        }
        composable(Destination.Groups.route) {
            val viewModel: GroupsViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            LaunchedEffect(viewModel) {
                viewModel.effects.collect { effect ->
                    when (effect) {
                        is GroupsEffect.OpenGroup -> navController.navigate(Destination.GroupDetails.createRoute(effect.groupId))
                    }
                }
            }
            GroupsScreen(
                state = state,
                onCreateGroup = { navController.navigate(Destination.CreateGroup.route) },
                onOpenGroup = { navController.navigate(Destination.GroupDetails.createRoute(it)) },
                onInviteCodeChange = viewModel::setInviteCode,
                onJoinByCode = viewModel::joinDemoGroup,
            )
        }
        composable(Destination.CreateGroup.route) {
            val viewModel: CreateGroupViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            LaunchedEffect(viewModel) {
                viewModel.created.collect { groupId ->
                    navController.navigate(Destination.GroupDetails.createRoute(groupId)) {
                        popUpTo(Destination.Groups.route)
                    }
                }
            }
            CreateGroupScreen(
                state,
                viewModel::updateName,
                viewModel::updateDescription,
                viewModel::updateObjective,
                viewModel::updateOwnerAlias,
                viewModel::selectMascot,
                viewModel::create,
            )
        }
        composable(
            route = Destination.GroupDetails.route,
            arguments = listOf(navArgument(Destination.GroupDetails.groupIdArgument) { type = NavType.StringType }),
        ) {
            val viewModel: GroupDetailsViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            LaunchedEffect(viewModel) {
                viewModel.closed.collect { navController.popBackStack(Destination.Groups.route, false) }
            }
            GroupDetailsScreen(
                state,
                viewModel::addDemoMember,
                viewModel::transferAdministration,
                viewModel::removeMember,
                viewModel::createGoal,
                viewModel::addCompletedSession,
                viewModel::leaveAsOwner,
            )
        }
        composable(Destination.Suggestion.route) {
            val viewModel: SuggestionViewModel = hiltViewModel()
            val item by viewModel.suggestion.collectAsStateWithLifecycle()
            LaunchedEffect(viewModel) {
                viewModel.effects.collect { effect ->
                    when (effect) {
                        is SuggestionEffect.OpenEditableSession -> navController.navigate(Destination.CreateSession.route)
                        SuggestionEffect.Close -> navController.popBackStack()
                    }
                }
            }
            SuggestionScreen(item, viewModel::accept, viewModel::adapt, viewModel::ignore)
        }
        composable(Destination.Institution.route) {
            InstitutionOverviewScreen(InstitutionalAggregateFixtures.mathematics)
        }
        composable(Destination.Settings.route) {
            val viewModel: SettingsViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            SettingsScreen(state, viewModel::deletePersonalHistory) { navController.navigate(Destination.UsageAccess.route) }
        }
        composable(Destination.UsageAccess.route) {
            val context = LocalContext.current
            val repository = androidx.compose.runtime.remember(context) { AndroidUsageStatsRepository(context.applicationContext) }
            UsageAccessSettingsScreen(
                isAvailable = repository.isAvailable(),
                hasPermission = repository.hasPermission(),
                onOpenSystemSettings = { context.startActivity(repository.createPermissionSettingsIntent()) },
                onDecline = { navController.popBackStack() },
            )
        }
    }
}
