package com.hackatudo.conscious.core.common

import android.util.Log

/** Logs only fixed technical event codes; callers cannot attach private free-form content. */
object TechnicalLogger {
    fun info(event: TechnicalEvent) = Log.i("ConsciousUse", event.name)
    fun error(event: TechnicalEvent, errorCode: String) = Log.e("ConsciousUse", "${event.name}:$errorCode")
}

enum class TechnicalEvent { DATABASE_OPENED, SYNC_STARTED, SYNC_FINISHED, WORK_RETRY_SCHEDULED }
