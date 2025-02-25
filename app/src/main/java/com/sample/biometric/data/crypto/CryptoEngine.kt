package com.sample.biometric.data.crypto

import android.security.keystore.KeyGenParameterSpec.Builder
import android.security.keystore.KeyProperties.BLOCK_MODE_CBC
import android.security.keystore.KeyProperties.ENCRYPTION_PADDING_PKCS7
import android.security.keystore.KeyProperties.KEY_ALGORITHM_AES
import android.security.keystore.KeyProperties.PURPOSE_DECRYPT
import android.security.keystore.KeyProperties.PURPOSE_ENCRYPT
import android.util.Base64
import com.sample.biometric.data.models.CryptoPurpose
import com.sample.biometric.data.models.CryptoPurpose.Decryption
import com.sample.biometric.data.models.EncryptedDataResult
import timber.log.Timber
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.KeyStore
import java.security.KeyStore.SecretKeyEntry
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

open class CryptoEngine(
    private val keyAlias: String = KEY_ALIAS
) {

    companion object {
        private const val KEY_SIZE = 16 * 8 // bytes
        private const val KEY_ALIAS = "KeyAlias"
        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
        private const val ALGORITHM = KEY_ALGORITHM_AES
        private const val BLOCK_MODE = BLOCK_MODE_CBC
        private const val PADDING = ENCRYPTION_PADDING_PKCS7
        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
    }

    private val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE).apply {
        load(null)
    }

    protected fun isKeyPresent(): Boolean {
        return keyStore.isKeyEntry(keyAlias)
    }

    @Throws(
        NullPointerException::class,
        NoSuchAlgorithmException::class,
        NoSuchProviderException::class,
        IllegalArgumentException::class,
        InvalidAlgorithmParameterException::class
    )
    protected fun createKey(buildConfig: (Builder) -> Builder): SecretKey? {
        Timber.d("KeyGenerator generateKey")
        return KeyGenerator.getInstance(ALGORITHM, ANDROID_KEY_STORE).apply {
            val builder = Builder(
                keyAlias,
                PURPOSE_ENCRYPT or PURPOSE_DECRYPT
            )
                .setKeySize(KEY_SIZE) // key size in bits
                .setBlockModes(BLOCK_MODE)
                .setEncryptionPaddings(PADDING)
            init(buildConfig(builder).build())
        }.generateKey()
    }

    @Throws(
        NullPointerException::class,
        NoSuchAlgorithmException::class,
        NoSuchProviderException::class,
        IllegalArgumentException::class,
        InvalidAlgorithmParameterException::class
    )
    protected open fun createKey(): SecretKey? = createKey {
        Timber.d("createKey randomizedEncryptionRequired=true")
        it.setRandomizedEncryptionRequired(true)
    }

    @Throws(
        NullPointerException::class,
        NoSuchAlgorithmException::class,
        UnrecoverableEntryException::class,
        UnrecoverableKeyException::class,
        KeyStoreException::class
    )
    private fun getKey(): SecretKey? {
        val existingKey = keyStore.getEntry(keyAlias, null) as? SecretKeyEntry
        if (existingKey == null) {
            Timber.d("KeyStore: getEntry is null")
        } else {
            Timber.d("KeyStore: getEntry found")
        }
        return existingKey?.secretKey ?: createKey()
    }

    @Throws(
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class,
        UnsupportedOperationException::class,
        InvalidKeyException::class,
        InvalidAlgorithmParameterException::class
    )
    protected fun initCipher(purpose: CryptoPurpose, iv: ByteArray? = null): Cipher {
        Timber.d("initCipher")
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val secretKey = getKey()
        if (purpose == Decryption) {
            requireNotNull(iv)
            cipher.init(DECRYPT_MODE, secretKey, IvParameterSpec(iv))
        } else {
            cipher.init(ENCRYPT_MODE, secretKey)
        }
        return cipher
    }

    fun encrypt(plainText: String): EncryptedDataResult {
        Timber.d("encrypt data")
        try {
            val cipher = initCipher(CryptoPurpose.Encryption)
            val encryptedData = cipher.doFinal(plainText.toByteArray())
            val iv = cipher.iv
            val encodedData = Base64.encodeToString(encryptedData, Base64.DEFAULT)
            val encodedIv = Base64.encodeToString(iv, Base64.DEFAULT)
            return EncryptedDataResult(encodedData, encodedIv)
        } catch (e: Exception) {
            Timber.e(e)
            return EncryptedDataResult(
                data = plainText,
                iv = ""
            )
        }
    }

    fun decrypt(encryptedData: EncryptedDataResult): String {
        Timber.d("decrypt data")
        if (encryptedData.data.isBlank() || encryptedData.iv.isBlank()) return encryptedData.data

        try {
            val iv = Base64.decode(encryptedData.iv, Base64.DEFAULT)
            val cipher = initCipher(Decryption, iv)
            val encryptedBytes = Base64.decode(encryptedData.data, Base64.DEFAULT)
            val decryptedBytes = cipher.doFinal(encryptedBytes)
            return String(decryptedBytes)
        } catch (e: Exception) {
            Timber.e(e)
            return ""
        }
    }

    fun removeKey() {
        if (isKeyPresent()) {
            keyStore.deleteEntry(keyAlias)
            Timber.d("crypto key removed")
        }
    }

}