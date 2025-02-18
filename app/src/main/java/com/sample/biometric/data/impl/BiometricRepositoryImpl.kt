package com.sample.biometric.data.impl

import android.util.Base64
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE
import androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED
import androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE
import androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
import androidx.biometric.BiometricPrompt.CryptoObject
import com.sample.biometric.common.CryptoPurpose
import com.sample.biometric.data.BiometricRepository
import com.sample.biometric.data.PreferenceRepository
import com.sample.biometric.data.crypto.CryptoEngine
import com.sample.biometric.data.crypto.ValidationResult
import com.sample.biometric.data.crypto.ValidationResult.KEY_INIT_FAIL
import com.sample.biometric.data.crypto.ValidationResult.KEY_PERMANENTLY_INVALIDATED
import com.sample.biometric.data.crypto.ValidationResult.OK
import com.sample.biometric.data.crypto.ValidationResult.VALIDATION_FAILED
import com.sample.biometric.data.error.InvalidCryptoLayerException
import com.sample.biometric.data.model.BiometricAuthStatus
import com.sample.biometric.data.model.BiometricAuthStatus.AVAILABLE_BUT_NOT_ENROLLED
import com.sample.biometric.data.model.BiometricAuthStatus.NOT_AVAILABLE
import com.sample.biometric.data.model.BiometricAuthStatus.TEMPORARY_NOT_AVAILABLE
import com.sample.biometric.data.model.BiometricInfo
import com.sample.biometric.data.model.BiometricInfo.KeyStatus.INVALIDATED
import com.sample.biometric.data.model.BiometricInfo.KeyStatus.NOT_READY
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class BiometricRepositoryImpl(
    private val biometricManager: BiometricManager,
    private val requiredAuthenticators: Int = BIOMETRIC_STRONG,
    private val preferenceRepository: PreferenceRepository,
    private val cryptoEngine: CryptoEngine,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BiometricRepository {

    companion object {
        const val BIOMETRIC_TOKEN_KEY = "BIOMETRIC_TOKEN"
        const val BIOMETRIC_IV_KEY = "BIOMETRIC_TOKEN_IV"
    }

    override suspend fun getBiometricInfo(): BiometricInfo = withContext(dispatcher) {
        val biometricAuthStatus = readBiometricAuthStatus()
        val cryptoValidationResult = checkInternalWithCrypto()
        val isBiometricTokenPresent = isTokenPresent()
        BiometricInfo(
            biometricTokenPresent = isBiometricTokenPresent,
            biometricAuthStatus = biometricAuthStatus,
            keyStatus = when (cryptoValidationResult) {
                OK -> BiometricInfo.KeyStatus.READY
                KEY_INIT_FAIL, VALIDATION_FAILED -> NOT_READY
                KEY_PERMANENTLY_INVALIDATED -> INVALIDATED
            }
        )
    }

    override suspend fun fetchAndStoreEncryptedToken(
        cryptoObject: CryptoObject,
        token: String
    ) = withContext(dispatcher) {
        validateCryptoLayer()
        // 2. encrypt the data using the cipher inside the cryptoObject
        val encryptedData = cryptoEngine.encrypt(token, cryptoObject)
        // 3. Store encrypted data and iv.
        encryptedData?.iv?.let { iv ->
            storeDataAndIv(encryptedData.data, iv)
        } ?: run {
            Timber.e("encryptedData or iv are null")
        }
    }

    private suspend fun checkInternalWithCrypto(): ValidationResult = withContext(dispatcher) {
        val validationResult = cryptoEngine.validate()
        when (validationResult) {
            KEY_PERMANENTLY_INVALIDATED,
            KEY_INIT_FAIL -> {
                // Delete data immediately is a policy that we have decided to implement: you have always to
                // notify this condition to the user
                clearCryptoAndData()
            }

            else -> {
                // Do nothing
            }
        }
        validationResult
    }

    private fun readBiometricAuthStatus() =
        when (biometricManager.canAuthenticate(requiredAuthenticators)) {
            BIOMETRIC_SUCCESS -> BiometricAuthStatus.READY
            BIOMETRIC_ERROR_NO_HARDWARE -> NOT_AVAILABLE
            BIOMETRIC_ERROR_HW_UNAVAILABLE -> TEMPORARY_NOT_AVAILABLE
            BIOMETRIC_ERROR_NONE_ENROLLED -> AVAILABLE_BUT_NOT_ENROLLED
            else -> NOT_AVAILABLE
        }

    private suspend fun storeDataAndIv(encryptedData: ByteArray, iv: ByteArray) {
        val dataBase64 = Base64.encodeToString(encryptedData, Base64.DEFAULT)
        val ivBase64 = Base64.encodeToString(iv, Base64.DEFAULT)
        preferenceRepository.storeValue(BIOMETRIC_TOKEN_KEY, dataBase64)
        preferenceRepository.storeValue(BIOMETRIC_IV_KEY, ivBase64)
    }

    override suspend fun decryptToken(cryptoObject: CryptoObject): String {
        validateCryptoLayer()
        // 1. read encrypted token (string base64 encoded)
        val token = preferenceRepository.getValue(BIOMETRIC_TOKEN_KEY)
        // 2. decode token data on byteArray
        val tokenData = Base64.decode(token, Base64.DEFAULT)
        // 3. decrypt token via cryptoEngine (using cipher inside cryptoObject
        return cryptoEngine.decrypt(tokenData, cryptoObject)
    }

    override suspend fun createCryptoObject(
        purpose: CryptoPurpose
    ): CryptoObject = withContext(dispatcher) {
        validateCryptoLayer()
        val iv = if (purpose == CryptoPurpose.Decryption) {
            Base64.decode(preferenceRepository.getValue(BIOMETRIC_IV_KEY), Base64.DEFAULT)
        } else {
            null
        }
        cryptoEngine.createCryptoObject(purpose, iv)
    }

    override suspend fun clear() {
        preferenceRepository.clear()
    }

    private suspend fun isTokenPresent(): Boolean =
        preferenceRepository.contains(BIOMETRIC_TOKEN_KEY)
            && preferenceRepository.contains(BIOMETRIC_IV_KEY)

    /**
     * Validate the crypto layer. In case of invalid status, this method
     * throws an [InvalidCryptoLayerException]
     */
    private suspend fun validateCryptoLayer() {
        val status = checkInternalWithCrypto()
        if (status != OK) {
            throw InvalidCryptoLayerException(status)
        }
    }

    private suspend fun clearCryptoAndData() {
        cryptoEngine.clear()
        preferenceRepository.clear()
    }

}