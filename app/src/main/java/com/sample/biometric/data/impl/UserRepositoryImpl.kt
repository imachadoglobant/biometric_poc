package com.sample.biometric.data.impl

import com.sample.biometric.data.PreferenceRepository
import com.sample.biometric.data.UserRepository

class UserRepositoryImpl(private val preferenceRepository: PreferenceRepository) : UserRepository {

    companion object {
        private const val TOKEN_KEY = "TOKEN_KEY"
        private const val USERNAME_KEY = "USERNAME_KEY"
    }

    override suspend fun saveUser(username: String, token: String) {
        preferenceRepository.storeValue(USERNAME_KEY, username)
        preferenceRepository.storeValue(TOKEN_KEY, token)
    }

    override suspend fun getToken(): String {
        return preferenceRepository.getValue(TOKEN_KEY)
    }

    override suspend fun getUsername(): String {
        return preferenceRepository.getValue(USERNAME_KEY)
    }

    override suspend fun logout() {
        preferenceRepository.clear()
    }

}

