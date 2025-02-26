package com.sample.biometric.data.models

/**
 * Used by [com.sample.biometric.data.crypto.BiometricCryptoEngine] to determine if a KeyStore's
 * [javax.crypto.SecretKey] was successfully created or warmup was successful, or if any exception
 * was thrown.
 */
enum class BiometricValidationResult {

    /**
     *  Indicates that [javax.crypto.SecretKey] was successfully created or a warmup was successful.
     */
    OK,

    /**
     * Indicates that some other type of Exception was thrown during key creation.
     */
    KEY_INIT_FAIL,

    /**
     * Indicates that [android.security.keystore.KeyPermanentlyInvalidatedException] was thrown.
     */
    KEY_PERMANENTLY_INVALIDATED,

    /**
     * Indicates that some other type of Exception was thrown during warmup.
     */
    VALIDATION_FAILED
}