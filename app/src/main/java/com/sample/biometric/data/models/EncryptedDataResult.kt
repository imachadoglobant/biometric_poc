package com.sample.biometric.data.models

import com.sample.biometric.data.models.EncryptedDataResult.Companion.SEPARATOR

/**
 * Used by [com.sample.biometric.data.crypto.CryptoEngine] to return encrypted data for storage.
 */
data class EncryptedDataResult(
    /**
     * Encrypted data
     */
    val data: String,
    /**
     * Initialization Vector (IV) to decrypt the data
     */
    val iv: String
) {

    companion object {
        internal const val SEPARATOR = "|"
    }

    override fun toString(): String {
        return "${data}$SEPARATOR${iv}"
    }

}

internal fun String.toEncryptedData(): EncryptedDataResult {
    val (data, iv) = this.split(SEPARATOR, limit = 2)
    return EncryptedDataResult(data, iv)
}