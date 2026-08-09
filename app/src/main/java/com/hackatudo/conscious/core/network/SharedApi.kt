package com.hackatudo.conscious.core.network

import com.google.gson.annotations.SerializedName
import java.util.UUID
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class LoginDto(val email: String, val password: String)
data class AuthResultDto(val accessToken: String, val expiresAt: String)

/** Explicit allowlist: private app, intention, UsageStats and reflection fields do not exist here. */
data class SharedSessionSummaryDto(
    val id: UUID,
    val groupId: UUID,
    val durationMinutes: Int,
    val completed: Boolean,
    val occurredAt: String,
)

interface SharedApi {
    @POST("auth/login") suspend fun login(@Body request: LoginDto): AuthResultDto
    @POST("session-summaries")
    suspend fun sendSummary(
        @Header("Idempotency-Key") idempotencyKey: String,
        @Body request: SharedSessionSummaryDto,
    ): Response<Unit>
}
