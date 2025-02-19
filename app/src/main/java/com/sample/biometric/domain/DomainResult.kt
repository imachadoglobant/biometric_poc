package com.sample.biometric.domain

sealed interface DomainResult<out T> {
    data class Loading(val initial: Boolean = false) : DomainResult<Nothing>
    data class Success<T>(val data: T?) : DomainResult<T>
    data class Error<T>(val exception: Throwable? = null) : DomainResult<T>

    fun successDataOrNull(): T? {
        if (this is Success) {
            return this.data
        }
        return null
    }
}

