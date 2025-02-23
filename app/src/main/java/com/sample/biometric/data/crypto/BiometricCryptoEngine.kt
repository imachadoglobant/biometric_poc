package com.sample.biometric.data.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties.BLOCK_MODE_CBC
import android.security.keystore.KeyProperties.ENCRYPTION_PADDING_PKCS7
import android.security.keystore.KeyProperties.KEY_ALGORITHM_AES
import android.security.keystore.KeyProperties.PURPOSE_DECRYPT
import android.security.keystore.KeyProperties.PURPOSE_ENCRYPT
import android.util.Base64
import androidx.biometric.BiometricPrompt.CryptoObject
import com.sample.biometric.data.crypto.ValidationResult.KEY_INIT_FAIL
import com.sample.biometric.data.crypto.ValidationResult.KEY_PERMANENTLY_INVALIDATED
import com.sample.biometric.data.crypto.ValidationResult.OK
import com.sample.biometric.data.crypto.ValidationResult.VALIDATION_FAILED
import com.sample.biometric.data.model.CryptoPurpose
import com.sample.biometric.data.model.CryptoPurpose.Decryption
import timber.log.Timber
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.Cipher.DECRYPT_MODE
import javax.crypto.Cipher.ENCRYPT_MODE
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import kotlin.random.Random
import kotlin.text.Charsets.UTF_8

class BiometricCryptoEngine {

    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val TARGET_KEY_ALIAS = "DefEncDecKey"
        private const val RANDOM_BYTE_ARRAY_SIZE = 16
        private const val CYPHER_TRANSFORMATION =
            "$KEY_ALGORITHM_AES/$BLOCK_MODE_CBC/$ENCRYPTION_PADDING_PKCS7"
    }

    private val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

    private fun doWarmupWithResult(): ValidationResult {
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

    private fun generateKeyWithResult(): ValidationResult {
        return try {
            generateTargetKey()
            OK
        } catch (e: Exception) {
            Timber.e(e, "generateTargetKey fail")
            KEY_INIT_FAIL
        }
    }

    private fun warmup() {
        createCryptoObject(Decryption, null)
    }

    private fun isTargetKeyPresent(): Boolean {
        return keyStore.isKeyEntry(TARGET_KEY_ALIAS)
    }

    private fun generateKeyInternal(keyGenParameterSpec: KeyGenParameterSpec) {
        val keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
    }

    private fun generateTargetKey() {
        generateKeyInternal(
            KeyGenParameterSpec.Builder(
                TARGET_KEY_ALIAS, // The alias (aka name) of the key
                PURPOSE_ENCRYPT or PURPOSE_DECRYPT
            )
                .setBlockModes(BLOCK_MODE_CBC)
                .setEncryptionPaddings(ENCRYPTION_PADDING_PKCS7)
                // this flag require that every key usage require a user authentication
                .setUserAuthenticationRequired(true)
                .setInvalidatedByBiometricEnrollment(true)
                .build()
        )
    }

    private fun getCipher(): Cipher {
        return Cipher.getInstance(CYPHER_TRANSFORMATION)
    }

    private fun getSecretKey(): SecretKey {
        return keyStore.getKey(TARGET_KEY_ALIAS, null) as SecretKey
    }

    private fun removeTargetKey() {
        if (isTargetKeyPresent()) {
            keyStore.deleteEntry(TARGET_KEY_ALIAS)
        }
    }

    fun validate(): ValidationResult {
        return if (!isTargetKeyPresent()) {
            generateKeyWithResult()
        } else {
            doWarmupWithResult()
        }
    }

    fun createCryptoObject(purpose: CryptoPurpose, iv: String?): CryptoObject {
        val decryptedIv = if (iv == null) {
            Random.nextBytes(ByteArray(RANDOM_BYTE_ARRAY_SIZE))
        } else if (purpose == Decryption) {
            Base64.decode(iv, Base64.DEFAULT)
        } else {
            null
        }
        val cipher = getCipher()
        val secretKey = getSecretKey()
        if (purpose == Decryption) {
            cipher.init(DECRYPT_MODE, secretKey, IvParameterSpec(decryptedIv))
        } else {
            cipher.init(ENCRYPT_MODE, secretKey)
        }
        return CryptoObject(cipher)
    }

    fun encrypt(clearText: String, cryptoObject: CryptoObject): EncryptDataResult? {
        val cipher = cryptoObject.cipher ?: return null
        val tokenData = clearText.toByteArray(UTF_8)
        val encryptedData = cipher.doFinal(tokenData)
        val iv = cipher.iv
        val dataBase64 = Base64.encodeToString(encryptedData, Base64.DEFAULT)
        val ivBase64 = Base64.encodeToString(iv, Base64.DEFAULT)
        return EncryptDataResult(
            data = dataBase64,
            iv = ivBase64
        )
    }

    fun decrypt(data: String, cryptoObject: CryptoObject): String {
        val decodedData = Base64.decode(data, Base64.DEFAULT)
        val cipher = cryptoObject.cipher
        val decryptedData = cipher?.doFinal(decodedData)
        return decryptedData?.toString(UTF_8).orEmpty()
    }

    fun clear() {
        removeTargetKey()
    }

}