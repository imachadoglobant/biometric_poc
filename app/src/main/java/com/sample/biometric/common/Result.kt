package com.sample.biometric.common

import kotlin.coroutines.cancellation.CancellationException

sealed interface Result<out T> {
    data class Loading(val initial: Boolean = false) : Result<Nothing>
    data class Success<T>(val data: T?) : Result<T>
    data class Error<T>(val exception: Throwable? = null) : Result<T>

    fun successDataOrNull(): T? {
        if (this is Success) {
            return this.data
        }
        return null
    }
}

internal inline fun <T> getResult(block: () -> T): Result<T> = try {
    block().let { Result.Success(it) }
} catch (e: Exception) {
    // propagate cancellation
    if (e is CancellationException) {
        throw e
    }
    Result.Error(e)
}

internal inline fun <T> Result<T>.switch(
    success: (T?) -> Unit = {},
    error: (e: Throwable?) -> Unit = {},
    loading: () -> Unit = {}
) {
    when (this) {
        is Result.Success -> success(this.data)
        is Result.Error -> error(this.exception)
        is Result.Loading -> loading()
    }
}

