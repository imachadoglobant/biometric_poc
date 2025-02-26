package com.sample.biometric.data.models

/**
 * Used by general and biometric encryption and decryption of data to determine the
 * [javax.crypto.Cipher] instance objective.
 */
enum class CryptoPurpose {
    Encryption,
    Decryption
}