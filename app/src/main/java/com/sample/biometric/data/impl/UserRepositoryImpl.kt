package com.sample.biometric.data.impl

import com.sample.biometric.data.UserDataDao
import com.sample.biometric.data.UserRepository
import com.sample.biometric.data.entities.UserDataEntity
import com.sample.biometric.data.model.UserData
import kotlinx.coroutines.flow.firstOrNull
import timber.log.Timber

class UserRepositoryImpl(private val dao: UserDataDao) : UserRepository {

    override suspend fun saveUser(userData: UserData): UserData {
        dao.insert(
            UserDataEntity(
                id = userData.id,
                username = userData.username,
                token = userData.token,
                expiredToken = userData.expiredToken
            )
        )
        Timber.d("Encoded user data saved")
        return userData
    }

    override suspend fun getUser(): UserData? {
        Timber.d("Decoded user data retrieved")
        val entity = dao.getFirst().firstOrNull() ?: return null
        return UserData(
            id = entity.id,
            username = entity.username,
            token = entity.token,
            expiredToken = entity.expiredToken
        )
    }

    override suspend fun saveBiometricData(biometricToken: String, iv: String) {
        Timber.d("Biometric user data saved")
        // preferenceRepository.storeValue(BIOMETRIC_TOKEN_KEY, biometricToken)
        // preferenceRepository.storeValue(BIOMETRIC_IV_KEY, iv)
    }

    override suspend fun isBiometricTokenPresent(): Boolean =
        false
        // preferenceRepository.contains(BIOMETRIC_TOKEN_KEY)
        //    && preferenceRepository.contains(BIOMETRIC_IV_KEY)

    override suspend fun getBiometricToken(): String {
        Timber.d("Biometric user data retrieved")
        // return preferenceRepository.getValue(BIOMETRIC_TOKEN_KEY)
        return ""
    }

    override suspend fun getBiometricIv(): String {
        // return preferenceRepository.getValue(BIOMETRIC_IV_KEY)
        return ""
    }

    override suspend fun expireToken(user: UserData?): UserData? {
        Timber.d("User token expired")
        val oldToken = user?.token ?: return null
        return saveUser(
            user.copy(
                token = "",
                expiredToken = oldToken
            )
        )
    }

    override suspend fun logout() {
        dao.deleteAll()
    }

}

