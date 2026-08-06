package com.hackatudo.conscious.core.common

sealed interface AppResult<out T> {
    data class Success<T>(val value: T) : AppResult<T>
    data class Failure(val error: AppError) : AppResult<Nothing>
}

sealed interface AppError {
    val userMessage: String

    data class Recoverable(override val userMessage: String) : AppError
    data class Unexpected(override val userMessage: String = "Não foi possível concluir a ação.") : AppError
}
