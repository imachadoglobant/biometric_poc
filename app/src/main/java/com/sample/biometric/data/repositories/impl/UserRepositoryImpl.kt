package com.sample.biometric.data.repositories.impl

import com.sample.biometric.data.dao.UserDataDao
import com.sample.biometric.data.crypto.CryptoEngine
import com.sample.biometric.data.models.EncryptedDataResult
import com.sample.biometric.data.entity.UserDataEntity
import com.sample.biometric.data.models.UserData
import com.sample.biometric.data.models.toEncryptedData
import com.sample.biometric.data.repositories.UserRepository
import kotlinx.coroutines.flow.firstOrNull
import timber.log.Timber

class UserRepositoryImpl(
    private val dao: UserDataDao,
    private val cryptoEngine: CryptoEngine
) : UserRepository {



    override suspend fun save(user: UserData): UserData {
        val username = cryptoEngine.encrypt(user.username).toString()
        val token = cryptoEngine.encrypt(user.token).toString()
        val expiredToken = cryptoEngine.encrypt(user.expiredToken).toString()

        dao.save(
            UserDataEntity(
                id = user.id,
                username = username,
                token = token,
                expiredToken = expiredToken,
                biometricToken = user.biometricToken,
                biometricIv = user.biometricIv
            )
        )
        Timber.d("User data saved")
        return user
    }

    override suspend fun retrieve(): UserData? {
        val entity = dao.getFirst().firstOrNull() ?: return null

        val username = cryptoEngine.decrypt(entity.username.toEncryptedData())
        val token = cryptoEngine.decrypt(entity.token.toEncryptedData())
        val expiredToken = cryptoEngine.decrypt(entity.expiredToken.toEncryptedData())

        Timber.d("User data retrieved")

        return UserData(
            id = entity.id,
            username = username,
            token = token,
            expiredToken = expiredToken,
            biometricToken = entity.biometricToken,
            biometricIv = entity.biometricIv
        )
    }

    override suspend fun logout() {
        dao.deleteAll()
        cryptoEngine.removeKey()
        Timber.d("User data destroyed")
    }

}

