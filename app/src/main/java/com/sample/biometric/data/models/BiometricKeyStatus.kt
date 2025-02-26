package com.sample.biometric.data.models

/**
 * Used by [BiometricStatus] to determine [javax.crypto.SecretKey] creating status.
 */
enum class BiometricKeyStatus {
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