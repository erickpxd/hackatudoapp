package com.hackatudo.conscious.app

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

@Singleton
class LauncherNavigationRequests @Inject constructor() {
    private val interventionChannel = Channel<String>(Channel.BUFFERED)
    val interventions = interventionChannel.receiveAsFlow()

    fun requestIntervention(packageName: String) {
        interventionChannel.trySend(packageName)
    }
}
