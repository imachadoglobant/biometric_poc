package com.sample.biometric.data.impl

import com.sample.biometric.data.PreferenceRepository
import com.sample.biometric.data.UserRepository

class UserRepositoryImpl(private val preferenceRepository: PreferenceRepository) : UserRepository {

    companion object {
        private const val TOKEN_KEY = "TOKEN_KEY"
        private const val USERNAME_KEY = "USERNAME_KEY"
        private const val BIOMETRIC_TOKEN_KEY = "BIOMETRIC_TOKEN"
        private const val BIOMETRIC_IV_KEY = "BIOMETRIC_TOKEN_IV"
    }

    override suspend fun saveUser(username: String, token: String) {
        preferenceRepository.storeValue(USERNAME_KEY, username)
        preferenceRepository.storeValue(TOKEN_KEY, token)
    }

    override suspend fun saveBiometricData(biometricToken: String, iv: String) {
        preferenceRepository.storeValue(BIOMETRIC_TOKEN_KEY, biometricToken)
        preferenceRepository.storeValue(BIOMETRIC_IV_KEY, iv)
    }

    override suspend fun getToken(): String {
        return preferenceRepository.getValue(TOKEN_KEY)
    }

    override suspend fun isTokenPresent(): Boolean =
        preferenceRepository.contains(BIOMETRIC_TOKEN_KEY)
            && preferenceRepository.contains(BIOMETRIC_IV_KEY)

    override suspend fun getBiometricToken(): String {
        return preferenceRepository.getValue(BIOMETRIC_TOKEN_KEY)
    }

    override suspend fun getBiometricIv(): String {
        return preferenceRepository.getValue(BIOMETRIC_IV_KEY)
    }

    override suspend fun getUsername(): String {
        return preferenceRepository.getValue(USERNAME_KEY)
    }

    override suspend fun logout() {
        preferenceRepository.clear()
    }

}

