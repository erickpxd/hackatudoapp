package com.hackatudo.conscious.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hackatudo.conscious.core.datastore.PrivacyPreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class OnboardingViewModel @Inject constructor(private val preferences: PrivacyPreferencesDataStore) : ViewModel() {
    private val effectsChannel = Channel<Unit>(Channel.BUFFERED)
    val completed = effectsChannel.receiveAsFlow()
    init { viewModelScope.launch { if (preferences.preferences.first().onboardingCompleted) effectsChannel.send(Unit) } }
    fun finish(userName: String, petName: String, themeColor: String) = viewModelScope.launch {
        preferences.setOnboardingProfile(userName.trim(), petName, themeColor)
        preferences.setLocalCategoriesDisclosed(true)
        preferences.setSharedCategoriesDisclosed(true)
        preferences.setHomeRoleEducationShown(true)
        preferences.setOnboardingCompleted(true)
        effectsChannel.send(Unit)
    }
}
