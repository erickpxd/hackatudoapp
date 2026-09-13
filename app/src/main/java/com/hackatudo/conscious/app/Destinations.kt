package com.hackatudo.conscious.app

sealed interface Destination {
    val route: String

    data object Onboarding : Destination { override val route = "onboarding" }
    data object Launcher : Destination { override val route = "launcher" }
    data object CreateSession : Destination { override val route = "session/create" }
    data object SelectApps : Destination { override val route = "session/apps" }
    data object ActiveSession : Destination { override val route = "session/active" }
    data object Intervention : Destination {
        const val packageNameArgument = "packageName"
        override val route = "intervention/{$packageNameArgument}"
        fun createRoute(packageName: String) = "intervention/${android.net.Uri.encode(packageName)}"
    }
    data object SessionSummary : Destination {
        const val sessionIdArgument = "sessionId"
        override val route = "session/summary/{$sessionIdArgument}"
        fun createRoute(sessionId: java.util.UUID) = "session/summary/$sessionId"
    }
    data object PersonalInsights : Destination { override val route = "insights" }
    data object Groups : Destination { override val route = "groups" }
    data object GroupDetails : Destination {
        const val groupIdArgument = "groupId"
        override val route = "groups/details/{$groupIdArgument}"
        fun createRoute(groupId: java.util.UUID) = "groups/details/$groupId"
    }
    data object CreateGroup : Destination { override val route = "groups/create" }
    data object Mascot : Destination { override val route = "mascot" }
    data object Settings : Destination { override val route = "settings" }
    data object Institution : Destination { override val route = "institution" }
    data object Suggestion : Destination { override val route = "session/suggestion" }
    data object UsageAccess : Destination { override val route = "settings/usage-access" }
    data object AiTutor : Destination { override val route = "ai-tutor" }
}
