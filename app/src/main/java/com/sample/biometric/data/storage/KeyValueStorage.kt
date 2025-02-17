package com.sample.biometric.data.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

class KeyValueStorage(context: Context) {

    companion object {
        private const val STORAGE_KEY = "STORAGE_KEY"
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(STORAGE_KEY)
    private val dataStore = context.dataStore

    suspend fun getValue(key: String): String {
        val preferencesKey = stringPreferencesKey(key)
        return dataStore.data.first()[preferencesKey].orEmpty()
    }

    suspend fun storeValue(key: String, value: String) {
        dataStore.edit { preference ->
            val preferencesKey = stringPreferencesKey(key)
            preference[preferencesKey] = value
        }
    }

    suspend fun clear() {
        dataStore.edit { preference ->
            preference.clear()
        }
    }

    suspend fun contains(key: String): Boolean {
        val preferencesKey = stringPreferencesKey(key)
        return dataStore.data.first().contains(preferencesKey)
    }

}