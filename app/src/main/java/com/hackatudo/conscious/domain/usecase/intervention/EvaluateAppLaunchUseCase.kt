package com.hackatudo.conscious.domain.usecase.intervention

import com.hackatudo.conscious.domain.model.AppLaunchEvaluation
import com.hackatudo.conscious.domain.repository.FocusSessionRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class EvaluateAppLaunchUseCase @Inject constructor(
    private val sessions: FocusSessionRepository,
) {
    suspend operator fun invoke(packageName: String): AppLaunchEvaluation {
        require(packageName.isNotBlank())
        val session = sessions.observeCurrent().first() ?: return AppLaunchEvaluation.ALLOW
        return if (packageName in session.selectedPackageNames) {
            AppLaunchEvaluation.ALLOW
        } else {
            AppLaunchEvaluation.INTERVENE
        }
    }
}
