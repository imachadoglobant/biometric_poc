package com.sample.biometric.data.models

data class EncryptedDataResult(
    val data: String,
    val iv: String
) {

    companion object {

        private const val SEPARATOR = "|"

        fun fromString(value: String) : EncryptedDataResult {
            val (data, iv) = value.split(SEPARATOR, limit = 2)
            return EncryptedDataResult(data, iv)
        }

    }

    override fun toString(): String {
        return "${data}$SEPARATOR${iv}"
    }

}
