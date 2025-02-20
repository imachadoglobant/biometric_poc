package com.sample.biometric.data.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.KeyProperties.PURPOSE_DECRYPT
import android.security.keystore.KeyProperties.PURPOSE_ENCRYPT
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.Cipher.DECRYPT_MODE
import javax.crypto.Cipher.ENCRYPT_MODE
import javax.crypto.KeyGenerator
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
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
    }

    private val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE).apply {
        load(null)
    }

    private val encryptCipher
        get() = Cipher.getInstance(TRANSFORMATION).apply {
            init(ENCRYPT_MODE, getKey())
        }

    private fun getDecryptCipherForIv(storedIv: ByteArray): Cipher {
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(DECRYPT_MODE, getKey(), IvParameterSpec(storedIv))
        }
    }

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

    private fun getKey(): SecretKey {
        val existingKey = keyStore.getEntry(ALIAS, null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createKey()
    }

    fun encrypt(plainText: String): EncryptedData {
        val secretKey = getKey()
        val cipher = encryptCipher
        cipher.init(ENCRYPT_MODE, secretKey)
        val encryptedData = cipher.doFinal(plainText.toByteArray())
        val iv = cipher.iv
        val encodedData = Base64.encodeToString(encryptedData, Base64.DEFAULT)
        val encodedIv = Base64.encodeToString(iv, Base64.DEFAULT)
        return EncryptedData(encodedData, encodedIv)
    }

    fun decrypt(encryptedText: String, storedIv: String): String {
        if (encryptedText.isEmpty() || storedIv.isEmpty()) return ""
        val iv = Base64.decode(storedIv, Base64.DEFAULT)
        val cipher = getDecryptCipherForIv(iv)
        val secretKeySpec = getKey()
        val ivParameterSpec = IvParameterSpec(iv)
        cipher.init(DECRYPT_MODE, secretKeySpec, ivParameterSpec)
        val encryptedBytes = Base64.decode(encryptedText, Base64.DEFAULT)
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes)
    }

}