package com.hackatudo.conscious.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
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
    val homeRolePromptDismissed: Boolean = false,
    val userName: String = "",
    val petName: String = "Neko",
    val themeColor: String = "blue",
    val notificationsEnabled: Boolean = true,
    val breakRemindersEnabled: Boolean = true,
    val currentContext: String = "school",
    val distractingPackages: Set<String> = emptySet(),
    val mutedNotificationPackages: Set<String> = emptySet(),
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
    suspend fun setHomeRolePromptDismissed(value: Boolean) = update(HOME_ROLE_PROMPT_DISMISSED, value)
    suspend fun setThemeColor(value: String) { context.privacyPreferences.edit { it[THEME_COLOR] = value } }
    suspend fun setNotificationsEnabled(value: Boolean) = update(NOTIFICATIONS_ENABLED, value)
    suspend fun setBreakRemindersEnabled(value: Boolean) = update(BREAK_REMINDERS_ENABLED, value)
    suspend fun setCurrentContext(value: String) { context.privacyPreferences.edit { it[CURRENT_CONTEXT] = value } }
    suspend fun setDistractingPackages(value: Set<String>) { context.privacyPreferences.edit { it[DISTRACTING_PACKAGES] = value } }
    suspend fun setMutedNotificationPackages(value: Set<String>) { context.privacyPreferences.edit { it[MUTED_NOTIFICATION_PACKAGES] = value } }
    suspend fun setOnboardingProfile(userName: String, petName: String, themeColor: String) {
        context.privacyPreferences.edit {
            it[USER_NAME] = userName
            it[PET_NAME] = petName
            it[THEME_COLOR] = themeColor
        }
    }


    private suspend fun update(key: Preferences.Key<Boolean>, value: Boolean) {
        context.privacyPreferences.edit { it[key] = value }
    }

    private fun Preferences.toDomain() = PrivacyPreferences(
        onboardingCompleted = this[ONBOARDING_COMPLETED] ?: false,
        localCategoriesDisclosed = this[LOCAL_DISCLOSED] ?: false,
        sharedCategoriesDisclosed = this[SHARED_DISCLOSED] ?: false,
        homeRoleEducationShown = this[HOME_EDUCATION_SHOWN] ?: false,
        homeRolePromptDismissed = this[HOME_ROLE_PROMPT_DISMISSED] ?: false,
        userName = this[USER_NAME] ?: "",
        petName = this[PET_NAME] ?: "Neko",
        themeColor = this[THEME_COLOR] ?: "blue",
        notificationsEnabled = this[NOTIFICATIONS_ENABLED] ?: true,
        breakRemindersEnabled = this[BREAK_REMINDERS_ENABLED] ?: true,
        currentContext = this[CURRENT_CONTEXT] ?: "school",
        distractingPackages = this[DISTRACTING_PACKAGES] ?: emptySet(),
        mutedNotificationPackages = this[MUTED_NOTIFICATION_PACKAGES] ?: emptySet(),
    )

    private companion object {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val LOCAL_DISCLOSED = booleanPreferencesKey("local_categories_disclosed")
        val SHARED_DISCLOSED = booleanPreferencesKey("shared_categories_disclosed")
        val HOME_EDUCATION_SHOWN = booleanPreferencesKey("home_role_education_shown")
        val HOME_ROLE_PROMPT_DISMISSED = booleanPreferencesKey("home_role_prompt_dismissed")
        val USER_NAME = stringPreferencesKey("user_name")
        val PET_NAME = stringPreferencesKey("pet_name")
        val THEME_COLOR = stringPreferencesKey("theme_color")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val BREAK_REMINDERS_ENABLED = booleanPreferencesKey("break_reminders_enabled")
        val CURRENT_CONTEXT = stringPreferencesKey("current_context")
        val DISTRACTING_PACKAGES = stringSetPreferencesKey("distracting_packages")
        val MUTED_NOTIFICATION_PACKAGES = stringSetPreferencesKey("muted_notification_packages")
    }
}
