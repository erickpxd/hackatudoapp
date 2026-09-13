package com.hackatudo.conscious.core.network

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteSessionStore @Inject constructor(@ApplicationContext context: Context) {
    private val preferences = context.getSharedPreferences("remote_session", Context.MODE_PRIVATE)

    fun token(): String? = preferences.getString(TOKEN, null)
    fun saveToken(token: String) { preferences.edit().putString(TOKEN, token).apply() }
    fun clear() { preferences.edit().remove(TOKEN).apply() }

    private companion object { const val TOKEN = "access_token" }
}
