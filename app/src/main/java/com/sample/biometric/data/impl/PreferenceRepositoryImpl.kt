package com.sample.biometric.data.impl

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sample.biometric.data.PreferenceRepository
import com.sample.biometric.data.crypto.CryptoManager
import kotlinx.coroutines.flow.firstOrNull
import timber.log.Timber

class PreferenceRepositoryImpl(
    context: Context,
    private val cryptoManager: CryptoManager
): PreferenceRepository {

    companion object {
        private const val PREFERENCE_NAME = "PREFERENCE_NAME"
        private const val SEPARATOR = "|"
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(PREFERENCE_NAME)
    private val dataStore = context.dataStore
    
    private suspend fun getData(): Preferences? {
        return try {
            dataStore.data.firstOrNull()
        } catch (e: IOException) {
            Timber.e("getData error", e)
            null
        }
    }

    override suspend fun getValue(key: String): String {
        val preferencesKey = stringPreferencesKey(key)
        return getData()?.get(preferencesKey).orEmpty()
    }

    override suspend fun storeValue(key: String, value: String) {
        dataStore.edit { preference ->
            val preferencesKey = stringPreferencesKey(key)
            preference[preferencesKey] = value
        }
    }

    override suspend fun getDecodedValue(key: String): String {
        val preferencesKey = stringPreferencesKey(key)
        val value = getData()?.get(preferencesKey).orEmpty()
        val (data, iv) = value.split(SEPARATOR, limit = 2)
        return cryptoManager.decrypt(data, iv)
    }

    override suspend fun storeEncodedValue(key: String, value: String) {
        val encryptedValue = cryptoManager.encrypt(value) ?: return
        storeValue(key, "${encryptedValue.data}$SEPARATOR${encryptedValue.iv}")
    }

    override suspend fun clear() {
        dataStore.edit { preference ->
            preference.clear()
        }
    }

    override suspend fun contains(key: String): Boolean {
        val preferencesKey = stringPreferencesKey(key)
        return getData()?.get(preferencesKey)?.isNotBlank() == true
    }

}