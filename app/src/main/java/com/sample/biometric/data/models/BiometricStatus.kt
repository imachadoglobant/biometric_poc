package com.sample.biometric.data.models

data class BiometricStatus(
    /**
     * True if our biometric token is present, false otherwise
     */
    val biometricTokenPresent: Boolean = false,

    /**
     * Give us information about the status of our biometric authentication
     */
    val biometricAuthStatus: BiometricAuthStatus,

    /**
     * Give us the status of our cryptographic key
     */
    val keyStatus: BiometricKeyStatus
){

    fun canAskAuthentication() = (biometricAuthStatus == BiometricAuthStatus.READY
            && keyStatus == BiometricKeyStatus.READY)

    fun canLoginWithBiometricToken() = biometricTokenPresent && canAskAuthentication()

}
