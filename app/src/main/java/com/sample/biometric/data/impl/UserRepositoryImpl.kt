package com.sample.biometric.data.impl

import android.content.Context
import com.sample.biometric.data.UserAuthenticationSerializer.userAuthenticationDataStore
import com.sample.biometric.data.UserRepository
import com.sample.biometric.data.model.UserData
import kotlinx.coroutines.flow.firstOrNull
import timber.log.Timber

class UserRepositoryImpl(context: Context) : UserRepository {

    private val dataStore = context.userAuthenticationDataStore

    override suspend fun logout() {
        try {
            dataStore.updateData { currentUser ->
                currentUser.toBuilder()
                    .clear()
                    .build()
            }
        } catch (e: Exception) {
            Timber.e("Error", "Error writing to proto store: $e")
        }
    }

    override suspend fun getUser(): UserData? {
        val userAuthentication = dataStore.data.firstOrNull() ?: return null
        return UserData(
            username = userAuthentication.username,
            token = userAuthentication.token,
            expiredToken = userAuthentication.expiredToken,
            biometricToken = userAuthentication.biometricToken,
            biometricIv = userAuthentication.biometricIv
        )
    }

    override suspend fun saveUser(userData: UserData): UserData? {
        try {
            dataStore.updateData { currentUser ->
                currentUser.toBuilder()
                    .setUsername(userData.username)
                    .setToken(userData.token)
                    .setExpiredToken(userData.expiredToken)
                    .setBiometricToken(userData.biometricToken)
                    .setBiometricIv(userData.biometricIv)
                    .build()
            }
            return userData
        } catch (e: Exception) {
            Timber.e("Error", "Error writing to proto store: $e")
            return null
        }
    }

}

