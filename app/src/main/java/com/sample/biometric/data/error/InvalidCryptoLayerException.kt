package com.sample.biometric.data.error

import com.sample.biometric.data.models.BiometricValidationResult
import com.sample.biometric.data.models.BiometricValidationResult.KEY_INIT_FAIL
import com.sample.biometric.data.models.BiometricValidationResult.KEY_PERMANENTLY_INVALIDATED

class InvalidCryptoLayerException(validationResult: BiometricValidationResult) : Exception() {

    val isKeyPermanentlyInvalidated = validationResult == KEY_PERMANENTLY_INVALIDATED
    val isKeyInitFailed = validationResult == KEY_INIT_FAIL

}
