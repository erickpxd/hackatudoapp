package com.hackatudo.conscious.domain.model

data class InstalledApp(
    val packageName: String,
    val displayName: String,
    val iconKey: String,
    val launchable: Boolean,
    val systemApp: Boolean,
)
