package com.sample.biometric.data.crypto

import android.security.keystore.KeyPermanentlyInvalidatedException
import android.util.Base64
import androidx.biometric.BiometricPrompt.CryptoObject
import com.sample.biometric.data.models.BiometricValidationResult.KEY_INIT_FAIL
import com.sample.biometric.data.models.BiometricValidationResult.KEY_PERMANENTLY_INVALIDATED
import com.sample.biometric.data.models.BiometricValidationResult.OK
import com.sample.biometric.data.models.BiometricValidationResult.VALIDATION_FAILED
import com.sample.biometric.data.models.CryptoPurpose
import com.sample.biometric.data.models.CryptoPurpose.Decryption
import com.sample.biometric.data.models.EncryptedDataResult
import com.sample.biometric.data.models.BiometricValidationResult
import timber.log.Timber
import javax.crypto.SecretKey
import kotlin.random.Random
import kotlin.text.Charsets.UTF_8

class BiometricCryptoEngine : CryptoEngine(BIOMETRIC_KEY_ALIAS) {

    companion object {
        private const val RANDOM_BYTE_ARRAY_SIZE = 16
        private const val BIOMETRIC_KEY_ALIAS = "BIOMETRIC_KEY_ALIAS"
    }

    /**
     * Configures [SecretKey] builder to require user authentication and key invalidation on
     * biometric enrollment.
     */
    override fun createKey(): SecretKey? = createKey {
        Timber.d("createKey userAuthenticationRequired=true invalidatedByBiometricEnrollment=true")
        it.setUserAuthenticationRequired(true)
        .setInvalidatedByBiometricEnrollment(true)
    }

    private fun doWarmupWithResult(): BiometricValidationResult {
        Timber.d("doWarmupWithResult")
        return try {
            warmup()
            OK
        } catch (e: KeyPermanentlyInvalidatedException) {
            Timber.e("KeyPermanentlyInvalidatedException", e)
            KEY_PERMANENTLY_INVALIDATED
        } catch (e: Exception) {
            Timber.e(e, "warmup unknown error", e)
            VALIDATION_FAILED
        }
    }

    private fun generateKeyWithResult(): BiometricValidationResult {
        return try {
            createKey()
            Timber.d("generateKeyWithResult ok")
            OK
        } catch (e: Exception) {
            Timber.e(e, "generateKeyWithResult fail")
            KEY_INIT_FAIL
        }
    }

    private fun warmup() {
        Timber.d("warmup")
        createCryptoObject(Decryption, null)
    }

    /**
     * Determines if [SecretKey] creation and warmup are successful.
     *
     * @return [BiometricValidationResult.OK] if [SecretKey] creation or warmup are successful.
     */
    fun validate(): BiometricValidationResult {
        return if (!isKeyPresent()) {
            generateKeyWithResult()
        } else {
            doWarmupWithResult()
        }
    }

    /**
     * Initializes [CryptoObject] for data encryption/decryption.
     *
     * @param purpose [CryptoPurpose] to determine [CryptoObject] objective.
     * @param iv Initialization Vector (IV) used for data decryption.
     * @return [CryptoObject] instance used for biometric data encryption/decryption.
     */
    fun createCryptoObject(purpose: CryptoPurpose, iv: String?): CryptoObject {
        Timber.d("createCryptoObject")
        val decryptedIv = if (iv == null) {
            Random.nextBytes(ByteArray(RANDOM_BYTE_ARRAY_SIZE))
        } else if (purpose == Decryption) {
            Base64.decode(iv, Base64.DEFAULT)
        } else {
            null
        }
        return CryptoObject(super.initCipher(purpose, decryptedIv))
    }

    /**
     * Encrypts given text using the provided [CryptoObject] instance.
     *
     * @param clearText Clear text data to encrypt.
     * @param cryptoObject [CryptoObject] instance used for biometric enrollment and encryption.
     * @return [EncryptedDataResult] if encryption is successful containing the encrypted data and
     * Initialization Vector (IV), or null if exception is thrown and the resulting cipher is null.
     */
    fun encrypt(clearText: String, cryptoObject: CryptoObject): EncryptedDataResult? {
        Timber.d("biometric encrypt")
        val cipher = cryptoObject.cipher ?: return null
        val tokenData = clearText.toByteArray(UTF_8)
        val encryptedData = cipher.doFinal(tokenData)
        val iv = cipher.iv
        val dataBase64 = Base64.encodeToString(encryptedData, Base64.DEFAULT)
        val ivBase64 = Base64.encodeToString(iv, Base64.DEFAULT)
        return EncryptedDataResult(
            data = dataBase64,
            iv = ivBase64
        )
    }

    /**
     * Decrypts given data using the provided [CryptoObject] instance.
     *
     * @param data Text to decrypt.
     * @param cryptoObject [CryptoObject] instance used during biometric authentication and data
     * decryption.
     * @return Decrypted text, or an empty String if exception is thrown.
     */
    fun decrypt(data: String, cryptoObject: CryptoObject): String {
        Timber.d("biometric decrypt")
        val decodedData = Base64.decode(data, Base64.DEFAULT)
        val cipher = cryptoObject.cipher
        val decryptedData = cipher?.doFinal(decodedData)
        return decryptedData?.toString(UTF_8).orEmpty()
    }

}