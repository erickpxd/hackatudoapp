package com.hackatudo.conscious.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.privacyPreferences by preferencesDataStore(name = "privacy_preferences")

data class PrivacyPreferences(
    val onboardingCompleted: Boolean = false,
    val localCategoriesDisclosed: Boolean = false,
    val sharedCategoriesDisclosed: Boolean = false,
    val homeRoleEducationShown: Boolean = false,
)

@Singleton
class PrivacyPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    val preferences: Flow<PrivacyPreferences> = context.privacyPreferences.data.map { it.toDomain() }

    suspend fun setOnboardingCompleted(value: Boolean) = update(ONBOARDING_COMPLETED, value)
    suspend fun setLocalCategoriesDisclosed(value: Boolean) = update(LOCAL_DISCLOSED, value)
    suspend fun setSharedCategoriesDisclosed(value: Boolean) = update(SHARED_DISCLOSED, value)
    suspend fun setHomeRoleEducationShown(value: Boolean) = update(HOME_EDUCATION_SHOWN, value)

    private suspend fun update(key: Preferences.Key<Boolean>, value: Boolean) {
        context.privacyPreferences.edit { it[key] = value }
    }

    private fun Preferences.toDomain() = PrivacyPreferences(
        onboardingCompleted = this[ONBOARDING_COMPLETED] ?: false,
        localCategoriesDisclosed = this[LOCAL_DISCLOSED] ?: false,
        sharedCategoriesDisclosed = this[SHARED_DISCLOSED] ?: false,
        homeRoleEducationShown = this[HOME_EDUCATION_SHOWN] ?: false,
    )

    private companion object {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val LOCAL_DISCLOSED = booleanPreferencesKey("local_categories_disclosed")
        val SHARED_DISCLOSED = booleanPreferencesKey("shared_categories_disclosed")
        val HOME_EDUCATION_SHOWN = booleanPreferencesKey("home_role_education_shown")
    }
}
