package com.sample.biometric.data.impl

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sample.biometric.data.PreferenceRepository
import com.sample.biometric.data.crypto.CryptoManager
import kotlinx.coroutines.flow.first

class PreferenceRepositoryImpl(
    context: Context,
    private val cryptoManager: CryptoManager
): PreferenceRepository {

    companion object {
        private const val STORAGE_KEY = "STORAGE_KEY"
        private const val IV_KEY = "IV_KEY"
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(STORAGE_KEY)
    private val dataStore = context.dataStore

    override suspend fun getValue(key: String): String {
        val preferencesKey = stringPreferencesKey(key)
        return dataStore.data.first()[preferencesKey].orEmpty()
    }

    override suspend fun storeValue(key: String, value: String) {
        dataStore.edit { preference ->
            val preferencesKey = stringPreferencesKey(key)
            preference[preferencesKey] = value
        }
    }

    override suspend fun getDecodedValue(key: String): String {
        val preferencesKey = stringPreferencesKey(key)
        val value = dataStore.data.first()[preferencesKey].orEmpty()
        val ivKey = stringPreferencesKey(IV_KEY)
        val iv = dataStore.data.first()[ivKey].orEmpty()
        return cryptoManager.decrypt(value, iv)
    }

    override suspend fun storeEncodedValue(key: String, value: String) {
        dataStore.edit { preference ->
            val preferencesKey = stringPreferencesKey(key)
            val ivKey = stringPreferencesKey(IV_KEY)
            val encryptedValue = cryptoManager.encrypt(value)
            preference[preferencesKey] = encryptedValue.data
            preference[ivKey] = encryptedValue.iv
        }
    }

    override suspend fun clear() {
        dataStore.edit { preference ->
            preference.clear()
        }
    }

    override suspend fun contains(key: String): Boolean {
        val preferencesKey = stringPreferencesKey(key)
        return dataStore.data.first()[preferencesKey]?.isNotBlank() == true
    }

}