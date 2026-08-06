package com.hackatudo.conscious.core.time

import android.os.SystemClock
import javax.inject.Inject

class AndroidClock @Inject constructor() : Clock {
    override fun nowEpochMillis(): Long = System.currentTimeMillis()
}

class AndroidMonotonicClock @Inject constructor() : MonotonicClock {
    override fun elapsedRealtimeMillis(): Long = SystemClock.elapsedRealtime()
}
