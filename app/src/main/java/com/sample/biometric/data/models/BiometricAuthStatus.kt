package com.sample.biometric.data.models

/**
 * Used by [BiometricStatus] to determine device biometric capabilities and if user has registered
 * biometric data on the system.
 */
enum class BiometricAuthStatus {
    /**
     *  We can interact with the biometric support.
     */
    READY,

    /**
     * Biometry support not present.
     */
    NOT_AVAILABLE,

    /**
     * Biometric support is currently unavailable.
     */
    TEMPORARY_NOT_AVAILABLE,

    /**
     * Biometric support is available, but no biometry has enrolled.
     */
    AVAILABLE_BUT_NOT_ENROLLED,
}