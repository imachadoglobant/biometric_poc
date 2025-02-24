package com.sample.biometric.data.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties.BLOCK_MODE_CBC
import android.security.keystore.KeyProperties.ENCRYPTION_PADDING_PKCS7
import android.security.keystore.KeyProperties.KEY_ALGORITHM_AES
import android.security.keystore.KeyProperties.PURPOSE_DECRYPT
import android.security.keystore.KeyProperties.PURPOSE_ENCRYPT
import android.util.Base64
import timber.log.Timber
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.security.UnrecoverableEntryException
import java.security.UnrecoverableKeyException
import javax.crypto.Cipher
import javax.crypto.Cipher.DECRYPT_MODE
import javax.crypto.Cipher.ENCRYPT_MODE
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class CryptoManager {

    data class EncryptedData(
        val data: String,
        val iv: String
    )

    companion object {
        private const val KEY_SIZE = 16 * 8 // bytes
        private const val ALIAS = "my_alias"
        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
        private const val ALGORITHM = KEY_ALGORITHM_AES
        private const val BLOCK_MODE = BLOCK_MODE_CBC
        private const val PADDING = ENCRYPTION_PADDING_PKCS7
        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
    }

    private val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE).apply {
        load(null)
    }

    @Throws(
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class,
        UnsupportedOperationException::class,
        InvalidKeyException::class
    )
    private fun getEncryptCipher(secretKey: SecretKey): Cipher? {
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(ENCRYPT_MODE, secretKey)
        }
    }

    @Throws(
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class,
        UnsupportedOperationException::class,
        InvalidKeyException::class,
        InvalidAlgorithmParameterException::class
    )
    private fun getDecryptCipherForIv(storedIv: ByteArray, secretKey: SecretKey): Cipher? {
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(DECRYPT_MODE, secretKey, IvParameterSpec(storedIv))
        }
    }

    @Throws(
        NullPointerException::class,
        NoSuchAlgorithmException::class,
        NoSuchProviderException::class,
        IllegalArgumentException::class,
        InvalidAlgorithmParameterException::class
    )
    private fun createKey(): SecretKey {
        return KeyGenerator.getInstance(ALGORITHM, ANDROID_KEY_STORE).apply {
            init(
                KeyGenParameterSpec.Builder(
                    ALIAS,
                    PURPOSE_ENCRYPT or PURPOSE_DECRYPT
                )
                    .setKeySize(KEY_SIZE) // key size in bits
                    .setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(PADDING)
                    .setUserAuthenticationRequired(false)
                    .setRandomizedEncryptionRequired(true)
                    .build()
            )
        }.generateKey()
    }

    @Throws(
        NullPointerException::class,
        NoSuchAlgorithmException::class,
        UnrecoverableEntryException::class,
        UnrecoverableKeyException::class,
        KeyStoreException::class
    )
    private fun getKey(): SecretKey {
        val existingKey = keyStore.getEntry(ALIAS, null) as? KeyStore.SecretKeyEntry
        if (existingKey == null) {
            Timber.d("existingKey is null")
        } else {
            Timber.d("existingKey found")
        }
        return existingKey?.secretKey ?: createKey()
    }

    fun encrypt(plainText: String): EncryptedData? {
        try {
            val secretKey = getKey()
            val cipher = getEncryptCipher(secretKey)
            requireNotNull(cipher)
            cipher.init(ENCRYPT_MODE, secretKey)
            val encryptedData = cipher.doFinal(plainText.toByteArray())
            val iv = cipher.iv
            val encodedData = Base64.encodeToString(encryptedData, Base64.DEFAULT)
            val encodedIv = Base64.encodeToString(iv, Base64.DEFAULT)
            return EncryptedData(encodedData, encodedIv)
        } catch (e: Exception) {
            Timber.e(e)
            return null
        }
    }

    fun decrypt(encryptedText: String, storedIv: String): String {
        try {
            require(encryptedText.isNotBlank())
            require(storedIv.isNotBlank())

            val iv = Base64.decode(storedIv, Base64.DEFAULT)
            val secretKey = getKey()
            val cipher = getDecryptCipherForIv(iv, secretKey)
            val ivParameterSpec = IvParameterSpec(iv)
            requireNotNull(cipher)
            cipher.init(DECRYPT_MODE, secretKey, ivParameterSpec)
            val encryptedBytes = Base64.decode(encryptedText, Base64.DEFAULT)
            val decryptedBytes = cipher.doFinal(encryptedBytes)
            return String(decryptedBytes)
        } catch (e: Exception) {
            Timber.e(e)
            return ""
        }
    }

}