package com.hackatudo.conscious.app

sealed interface Destination {
    val route: String

    data object Onboarding : Destination { override val route = "onboarding" }
    data object Launcher : Destination { override val route = "launcher" }
    data object CreateSession : Destination { override val route = "session/create" }
    data object SelectApps : Destination { override val route = "session/apps" }
    data object ActiveSession : Destination { override val route = "session/active" }
    data object Intervention : Destination { override val route = "intervention" }
    data object SessionSummary : Destination { override val route = "session/summary" }
    data object PersonalInsights : Destination { override val route = "insights" }
    data object Groups : Destination { override val route = "groups" }
    data object GroupDetails : Destination { override val route = "groups/details" }
    data object CreateGroup : Destination { override val route = "groups/create" }
    data object Mascot : Destination { override val route = "mascot" }
    data object Settings : Destination { override val route = "settings" }
    data object Institution : Destination { override val route = "institution" }
}
