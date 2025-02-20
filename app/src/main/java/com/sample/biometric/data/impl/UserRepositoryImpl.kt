package com.sample.biometric.data.impl

import com.sample.biometric.data.PreferenceRepository
import com.sample.biometric.data.UserRepository
import com.sample.biometric.data.model.UserData

class UserRepositoryImpl(private val preferenceRepository: PreferenceRepository) : UserRepository {

    companion object {
        private const val TOKEN_KEY = "TOKEN_KEY"
        private const val EXPIRED_TOKEN_KEY = "EXPIRED_TOKEN_KEY"
        private const val USERNAME_KEY = "USERNAME_KEY"
        private const val BIOMETRIC_TOKEN_KEY = "BIOMETRIC_TOKEN"
        private const val BIOMETRIC_IV_KEY = "BIOMETRIC_TOKEN_IV"
    }

    override suspend fun saveUser(username: String, token: String): UserData {
        preferenceRepository.storeEncodedValue(USERNAME_KEY, username)
        preferenceRepository.storeValue(TOKEN_KEY, token)
        preferenceRepository.storeValue(EXPIRED_TOKEN_KEY, "")

        return UserData(
            username = username,
            token = token,
            expiredToken = ""
        )
    }

    override suspend fun getUser(): UserData {
        return UserData(
            username = preferenceRepository.getDecodedValue(USERNAME_KEY),
            token = preferenceRepository.getValue(TOKEN_KEY),
            expiredToken = preferenceRepository.getValue(EXPIRED_TOKEN_KEY)
        )
    }

    override suspend fun saveBiometricData(biometricToken: String, iv: String) {
        preferenceRepository.storeValue(BIOMETRIC_TOKEN_KEY, biometricToken)
        preferenceRepository.storeValue(BIOMETRIC_IV_KEY, iv)
    }

    override suspend fun isBiometricTokenPresent(): Boolean =
        preferenceRepository.contains(BIOMETRIC_TOKEN_KEY)
            && preferenceRepository.contains(BIOMETRIC_IV_KEY)

    override suspend fun getBiometricToken(): String {
        return preferenceRepository.getValue(BIOMETRIC_TOKEN_KEY)
    }

    override suspend fun getBiometricIv(): String {
        return preferenceRepository.getValue(BIOMETRIC_IV_KEY)
    }

    override suspend fun expireToken() {
        val oldToken = preferenceRepository.getValue(TOKEN_KEY)
        preferenceRepository.storeValue(EXPIRED_TOKEN_KEY, oldToken)
        preferenceRepository.storeValue(TOKEN_KEY, "")
    }

    override suspend fun logout() {
        preferenceRepository.clear()
    }

}

