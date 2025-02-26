package com.sample.biometric.data.models

/**
 * Used by [com.sample.biometric.data.repositories.BiometricRepository] to determine device
 * biometric capabilities and if user has registered biometric data on the system.
 */
data class BiometricStatus(
    /**
     * True if our biometric token is present, false otherwise.
     */
    val biometricTokenPresent: Boolean = false,

    /**
     * Give us information about the status of our biometric authentication.
     */
    val biometricAuthStatus: BiometricAuthStatus,

    /**
     * Give us the status of our cryptographic key.
     */
    val keyStatus: BiometricKeyStatus
){

    /**
     * Returns TRUE if device is capable of biometric authentication, if user has registered
     * biometric authentication, and a [javax.crypto.SecretKey] was successfully created.
     */
    fun canAskAuthentication() = (biometricAuthStatus == BiometricAuthStatus.READY
            && keyStatus == BiometricKeyStatus.READY)

    /**
     * Returns TRUE if user has previously enrolled biometric authentication on the app.
     */
    fun canLoginWithBiometricToken() = biometricTokenPresent && canAskAuthentication()

    /**
     * Returns TRUE if user hasn't enrolled to biometric authentication on the system.
     */
    fun shouldEnrollBiometric() = biometricAuthStatus == BiometricAuthStatus.AVAILABLE_BUT_NOT_ENROLLED

}
