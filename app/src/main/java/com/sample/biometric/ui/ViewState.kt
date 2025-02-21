package com.sample.biometric.ui

sealed interface ViewState<out T> {
    object Initial : ViewState<Nothing>
    data class Loading(val initial: Boolean = false) : ViewState<Nothing>
    data class Success<T>(val data: T?) : ViewState<T>
    data class Error<T>(val exception: Throwable? = null) : ViewState<T>

    fun successDataOrNull(): T? {
        if (this is Success) {
            return this.data
        }
        return null
    }

}

