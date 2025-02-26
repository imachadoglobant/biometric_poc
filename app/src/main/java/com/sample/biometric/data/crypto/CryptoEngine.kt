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

/**
 * Abstracts data encryption and decryption logic by integrating Android KeyStore system.
 * Reduces the risk of unauthorized use of key material from outside the Android device by 
 * preventing the extraction of the key material from application processes and from the Android
 * device as a whole. Prevents unauthorized use of key material within the Android 
 * device by making apps specify the authorized uses of their keys and then enforcing those 
 * restrictions outside of the apps' processes. Protects keys from extraction and unauthorized use, 
 * even if the application code or the device’s operating system is compromised.
 */
open class CryptoEngine(private val keyAlias: String = KEY_ALIAS) {

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

    /**
     * Checks if key exists
     *
     * @return True if key exists
     * @throws KeyStoreException if the keystore has not been initialized (loaded).
     */
    @Throws(KeyStoreException::class)
    protected fun isKeyPresent(): Boolean {
        return keyStore.isKeyEntry(keyAlias)
    }

    /**
     * Generates a new AES key applying the given transformation.
     *
     * @param buildConfig Lambda to transform the Builder instance
     * @return The generated SecretKey object.
     * @throws NullPointerException if the specified algorithm is null.
     * @throws NoSuchAlgorithmException if a KeyGeneratorSpi implementation for the specified
     * algorithm is not available from the specified provider.
     * @throws NoSuchProviderException if the specified provider is not registered in the security 
     * provider list.
     * @throws IllegalArgumentException if the provider is null or empty.
     * @throws InvalidAlgorithmParameterException if the given parameters are inappropriate for 
     * this key generator.
     */
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
                .setBlockModes(BLOCK_MODE)
                .setEncryptionPaddings(PADDING)
                .setKeySize(KEY_SIZE) // key size in bits
            init(buildConfig(builder).build())
        }.generateKey()
    }

    /**
     * Generates a new AES key.
     *
     * @return The generated SecretKey object.
     * @throws NullPointerException if the specified algorithm is null.
     * @throws NoSuchAlgorithmException if a KeyGeneratorSpi implementation for the specified
     * algorithm is not available from the specified provider.
     * @throws NoSuchProviderException if the specified provider is not registered in the security
     * provider list.
     * @throws IllegalArgumentException if the provider is null or empty.
     * @throws InvalidAlgorithmParameterException if the given parameters are inappropriate for 
     * this key generator.
     */
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

    /**
     * Retrieves or generates a new AES key.
     *
     * @return The generated SecretKey object.
     * @throws NullPointerException if alias is null.
     * @throws NoSuchAlgorithmException if the algorithm for recovering the entry cannot be found.
     * @throws UnrecoverableEntryException if the specified protParam were insufficient or 
     * invalid.
     * @throws UnrecoverableKeyException if the entry is a [java.security.KeyStore.PrivateKeyEntry]
     * or [SecretKeyEntry] and the specified protParam does not contain the information needed to
     * recover the key (e.g. wrong password).
     * @throws KeyStoreException if the keystore has not been initialized (loaded).
     */
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
            return createKey()
        }

        Timber.d("KeyStore: getEntry found")
        return existingKey.secretKey
    }

    /**
     * Initializes the cipher object for data encryption/decryption.
     *
     * @param purpose A CryptoPurpose instance.
     * @param iv required for decryption.
     * @return The generated Cipher object.
     * @throws NoSuchAlgorithmException if transformation is null, empty, in an invalid format, or 
     * if no Provider supports a CipherSpi implementation for the specified algorithm, or if the
     * algorithm for recovering the entry cannot be found.
     * @throws NoSuchPaddingException if transformation contains a padding scheme that is not
     * available.
     * @throws NullPointerException if alias is null, or [purpose] is [Decryption] and [iv] is null.
     * @throws UnrecoverableEntryException if the specified protParam were insufficient or
     * invalid.
     * @throws UnrecoverableKeyException if the entry is a [java.security.KeyStore.PrivateKeyEntry]
     * or [SecretKeyEntry] and the specified protParam does not contain the information needed to
     * recover the key (e.g. wrong password).
     * @throws KeyStoreException if the keystore has not been initialized (loaded).
     * @throws UnsupportedOperationException if opmode is WRAP_MODE or UNWRAP_MODE but the
     * mode is not implemented by the underlying CipherSpi.
     * @throws InvalidKeyException if the given key is inappropriate for initializing this cipher,
     * or its keysize exceeds the maximum allowable keysize (as determined from the configured
     * jurisdiction policy files).
     * @throws InvalidAlgorithmParameterException if the given algorithm parameters are
     * inappropriate for this cipher, or this cipher requires algorithm parameters and params is
     * null, or the given algorithm parameters imply a cryptographic strength that would exceed the
     * legal limits (as determined from the configured jurisdiction policy files).
     */
    @Throws(
        NoSuchAlgorithmException::class,
        NullPointerException::class,
        UnrecoverableEntryException::class,
        UnrecoverableKeyException::class,
        KeyStoreException::class,
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

    /**
     * Encrypts the given text using KeyStore and cipher object.
     *
     * @param plainText Text to encrypt.
     * @return Result of the encryption process, the encrypted data and the Initialization Vector
     * (IV), or an empty object on failure.
     */
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

    /**
     * Decrypts the given text using KeyStore cipher object.
     *
     * @param encryptedData Data encrypted to decrypt.
     * @return Decoded data in plain text or the original data on failure.
     */
    fun decrypt(encryptedData: EncryptedDataResult): String {
        Timber.d("decrypt data")

        if (encryptedData.data.isNotBlank() && encryptedData.iv.isNotBlank()) {
            try {
                val iv = Base64.decode(encryptedData.iv, Base64.DEFAULT)
                val cipher = initCipher(Decryption, iv)
                val encryptedBytes = Base64.decode(encryptedData.data, Base64.DEFAULT)
                val decryptedBytes = cipher.doFinal(encryptedBytes)
                return String(decryptedBytes)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
        return encryptedData.data
    }

    /**
     * Clears existing on key.
     */
    fun removeKey() {
        if (isKeyPresent()) {
            keyStore.deleteEntry(keyAlias)
            Timber.d("crypto key removed")
        }
    }

}