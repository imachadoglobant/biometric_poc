package com.sample.biometric.data.repositories.impl

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE
import androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED
import androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE
import androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
import androidx.biometric.BiometricPrompt.CryptoObject
import com.sample.biometric.common.DataResult
import com.sample.biometric.common.DataResult.Error
import com.sample.biometric.common.DataResult.Success
import com.sample.biometric.data.crypto.BiometricCryptoEngine
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
import com.sample.biometric.data.model.BiometricStatus
import com.sample.biometric.data.model.CryptoPurpose
import com.sample.biometric.data.model.KeyStatus
import com.sample.biometric.data.model.KeyStatus.INVALIDATED
import com.sample.biometric.data.model.KeyStatus.NOT_READY
import com.sample.biometric.data.repositories.BiometricRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class BiometricRepositoryImpl(
    private val biometricManager: BiometricManager,
    private val cryptoEngine: BiometricCryptoEngine,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BiometricRepository {

    companion object {
        private const val REQUIRED_AUTHENTICATORS: Int = BIOMETRIC_WEAK
    }

    override suspend fun getBiometricStatus(isTokenPresent: Boolean): BiometricStatus =
        withContext(dispatcher) {
            val biometricAuthStatus = readBiometricAuthStatus()
            Timber.d("biometricAuthStatus=$biometricAuthStatus")
            val cryptoValidationResult = checkInternalWithCrypto()
            Timber.d("cryptoValidationResult=$cryptoValidationResult")

            BiometricStatus(
                biometricTokenPresent = isTokenPresent,
                biometricAuthStatus = biometricAuthStatus,
                keyStatus = when (cryptoValidationResult) {
                    OK -> KeyStatus.READY
                    KEY_INIT_FAIL, VALIDATION_FAILED -> NOT_READY
                    KEY_PERMANENTLY_INVALIDATED -> INVALIDATED
                }
            )
        }

    override suspend fun getEncryptedToken(
        cryptoObject: CryptoObject,
        token: String
    ) = withContext(dispatcher) {
        val error = validateCryptoLayer() as? Error
        if (error != null) return@withContext Error(error.exception)

        // Encrypt the data using the cipher inside the cryptoObject
        val encryptedData = cryptoEngine.encrypt(token, cryptoObject)
        Timber.d("getEncryptedToken")
        return@withContext Success(encryptedData)
    }

    private suspend fun checkInternalWithCrypto(): ValidationResult = withContext(dispatcher) {
        val validationResult = cryptoEngine.validate()
        when (validationResult) {
            KEY_PERMANENTLY_INVALIDATED,
            KEY_INIT_FAIL -> {
                Timber.e("checkInternalWithCrypto: error=$validationResult")
                clear()
            }

            else -> {
                Timber.d("checkInternalWithCrypto: success")
                // Do nothing
            }
        }
        validationResult
    }

    private fun readBiometricAuthStatus() =
        when (biometricManager.canAuthenticate(REQUIRED_AUTHENTICATORS)) {
            BIOMETRIC_SUCCESS -> BiometricAuthStatus.READY
            BIOMETRIC_ERROR_NO_HARDWARE -> NOT_AVAILABLE
            BIOMETRIC_ERROR_HW_UNAVAILABLE -> TEMPORARY_NOT_AVAILABLE
            BIOMETRIC_ERROR_NONE_ENROLLED -> AVAILABLE_BUT_NOT_ENROLLED
            else -> NOT_AVAILABLE
        }

    override suspend fun decryptToken(
        cryptoObject: CryptoObject,
        biometricToken: String
    ): DataResult<String> {
        val error = validateCryptoLayer() as? Error
        if (error != null) return Error(error.exception)

        // Decrypt token via cryptoEngine (using cipher inside cryptoObject
        Timber.d("decryptToken")
        return Success(cryptoEngine.decrypt(biometricToken, cryptoObject))
    }

    override suspend fun createCryptoObject(
        purpose: CryptoPurpose,
        iv: String
    ): DataResult<CryptoObject> = withContext(dispatcher) {
        val error = validateCryptoLayer() as? Error
        if (error != null) return@withContext Error(error.exception)

        Timber.d("createCryptoObject")
        return@withContext Success(cryptoEngine.createCryptoObject(purpose, iv))
    }

    override suspend fun clear() {
        Timber.d("clear")
        cryptoEngine.removeKey()
    }

    /**
     * Validate the crypto layer. In case of invalid status
     */
    private suspend fun validateCryptoLayer(): DataResult<Unit> {
        val status = checkInternalWithCrypto()

        return if (status != OK) {
            return Error(InvalidCryptoLayerException(status))
        } else {
            Success(Unit)
        }
    }

}