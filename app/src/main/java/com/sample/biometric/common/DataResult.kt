package com.sample.biometric.common

sealed interface DataResult<out T> {
    data class Success<T>(val data: T?) : DataResult<T>
    data class Error<T>(val exception: Throwable) : DataResult<T>

    fun successDataOrNull(): T? {
        if (this is Success) {
            return this.data
        }
        return null
    }
}

