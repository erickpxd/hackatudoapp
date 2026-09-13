package com.hackatudo.conscious.domain.usecase.intervention

import com.hackatudo.conscious.domain.model.AppLaunchEvaluation
import com.hackatudo.conscious.domain.repository.FocusSessionRepository
import com.hackatudo.conscious.core.datastore.PrivacyPreferencesDataStore
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class EvaluateAppLaunchUseCase @Inject constructor(
    private val sessions: FocusSessionRepository,
    private val preferences: PrivacyPreferencesDataStore,
) {
    suspend operator fun invoke(packageName: String): AppLaunchEvaluation {
        require(packageName.isNotBlank())
        val session = sessions.observeCurrent().first() ?: return AppLaunchEvaluation.ALLOW
        val profile = preferences.preferences.first()
        return if (packageName in session.selectedPackageNames ||
            (profile.currentContext != "school" && packageName !in profile.distractingPackages)) {
            AppLaunchEvaluation.ALLOW
        } else {
            AppLaunchEvaluation.INTERVENE
        }
    }
}
