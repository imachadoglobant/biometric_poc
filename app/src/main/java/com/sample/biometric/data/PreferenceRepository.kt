package com.sample.biometric.data

interface PreferenceRepository {

    suspend fun getValue(key: String): String

    suspend fun getDecodedValue(key: String): String

    suspend fun storeEncodedValue(key: String, value: String)

    suspend fun storeValue(key: String, value: String)

    suspend fun clear()

    suspend fun contains(key: String): Boolean

}