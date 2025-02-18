package com.sample.biometric.data.error

import com.sample.biometric.data.crypto.ValidationResult
import com.sample.biometric.data.crypto.ValidationResult.KEY_INIT_FAIL
import com.sample.biometric.data.crypto.ValidationResult.KEY_PERMANENTLY_INVALIDATED

class InvalidCryptoLayerException(validationResult: ValidationResult) : Exception() {

    val isKeyPermanentlyInvalidated = validationResult == KEY_PERMANENTLY_INVALIDATED
    val isKeyInitFailed = validationResult == KEY_INIT_FAIL

}
