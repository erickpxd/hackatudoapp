package com.hackatudo.conscious.feature.groups

data class SyncStatusUiState(
    val pending: Int = 0,
    val synced: Int = 0,
    val failed: Int = 0,
    val requiresAdministrativeDecision: Boolean = false,
) {
    val message: String get() = when {
        requiresAdministrativeDecision -> "O grupo mudou. Revise a versão atual antes de decidir novamente."
        failed > 0 -> "$failed contribuição(ões) aguardam nova tentativa."
        pending > 0 -> "$pending contribuição(ões) aguardam conexão."
        else -> "Contribuições sincronizadas."
    }
}
