package com.sample.biometric.data

import androidx.biometric.BiometricPrompt.CryptoObject
import com.sample.biometric.common.DataResult
import com.sample.biometric.data.crypto.EncryptDataResult
import com.sample.biometric.data.model.BiometricStatus
import com.sample.biometric.data.model.CryptoPurpose

/**
 * Represent the repository for our biometric related data / info
 */
interface BiometricRepository {

    /**
     * Read the biometric info that contains the biometric authentication
     * state, the underling key status and a flat to inform when the token is
     * already present
     *
     * @param isTokenPresent if user has been enrolled with biometric
     * @return the biometric info object
     */
    suspend fun getBiometricStatus(isTokenPresent: Boolean): BiometricStatus

    /**
     * Store the token using the [cryptoObject] passed as parameter.
     *
     * @param cryptoObject the cryptoObject to use for encryption operations
     * @throws com.sample.biometric.data.error.InvalidCryptoLayerException if
     * crypto layer is invalid
     */
    suspend fun getEncryptedToken(
        cryptoObject: CryptoObject,
        token: String
    ): DataResult<EncryptDataResult>

    /**
     * Decrypt the token using the [cryptoObject] passed as parameter
     *
     * @param cryptoObject the cryptoObject to use for decryption operations
     * @param biometricToken the encrypted token to decrypt
     * @return the token as string
     * @throws com.sample.biometric.data.error.InvalidCryptoLayerException if
     * crypto layer is invalid
     */
    suspend fun decryptToken(
        cryptoObject: CryptoObject,
        biometricToken: String
    ): DataResult<String>

    /**
     * Create a new [CryptoObject] instance for the specified purpose
     *
     * @param purpose the final purpose of the required cryptoObject
     * @param iv encrypted iv as string
     * @throws com.sample.biometric.data.error.InvalidCryptoLayerException if
     * crypto layer is invalid
     */
    suspend fun createCryptoObject(
        purpose: CryptoPurpose,
        iv: String
    ): DataResult<CryptoObject>

    /**
     * Clear the stored information
     */
    suspend fun clear()
}