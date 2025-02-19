package com.sample.biometric.data.model

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
    val keyStatus: KeyStatus
){

    fun canAskAuthentication() = (biometricAuthStatus == BiometricAuthStatus.READY
            && keyStatus == KeyStatus.READY)

    fun canLoginWithBiometricToken() = biometricTokenPresent && canAskAuthentication()

}
