package com.hackatudo.conscious.core.launcher

import android.content.Context
import com.hackatudo.conscious.core.common.AppError
import com.hackatudo.conscious.core.common.AppResult
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AndroidAppLauncher @Inject constructor(
    @ApplicationContext private val context: Context,
) : AppLauncher {
    override fun launch(packageName: String): AppResult<Unit> {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            ?: return AppResult.Failure(AppError.Recoverable("Este aplicativo não está disponível."))
        return runCatching {
            context.startActivity(intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK))
            AppResult.Success(Unit)
        }.getOrElse {
            AppResult.Failure(AppError.Recoverable("Não foi possível abrir este aplicativo."))
        }
    }
}
