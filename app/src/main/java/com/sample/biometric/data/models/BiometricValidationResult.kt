package com.sample.biometric.data.models

enum class BiometricValidationResult {
    OK,
    KEY_INIT_FAIL,
    KEY_PERMANENTLY_INVALIDATED,
    VALIDATION_FAILED
}