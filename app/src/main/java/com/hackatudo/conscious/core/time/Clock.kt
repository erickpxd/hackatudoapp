package com.hackatudo.conscious.core.time

interface Clock {
    fun nowEpochMillis(): Long
}

interface MonotonicClock {
    fun elapsedRealtimeMillis(): Long
}
