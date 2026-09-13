package com.hackatudo.conscious.core.network

import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor @Inject constructor(private val sessionStore: RemoteSessionStore) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val token = sessionStore.token()
        return chain.proceed(if (token == null) request else request.newBuilder().header("Authorization", "Bearer $token").build())
    }
}
