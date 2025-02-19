package com.sample.biometric.data.model

enum class KeyStatus {
    /**
     * Key is not ready to use
     */
    NOT_READY,

    /**
     * Key is ready
     */
    READY,

    /**
     * Key is present but permanently invalidate
     */
    INVALIDATED
}