package com.sample.biometric.di

import androidx.biometric.BiometricManager
import com.sample.biometric.data.BiometricRepository
import com.sample.biometric.data.UserRepository
import com.sample.biometric.data.crypto.CryptoEngine
import com.sample.biometric.data.impl.BiometricRepositoryImpl
import com.sample.biometric.data.impl.UserRepositoryImpl
import com.sample.biometric.data.storage.KeyValueStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepoModule {

    @Provides
    @Singleton
    fun provideUserRepository(keyValueStorage: KeyValueStorage): UserRepository {
        return UserRepositoryImpl(keyValueStorage)
    }

    @Provides
    @Singleton
    fun provideTokenRepository(
        biometricManager: BiometricManager,
        keyValueStorage: KeyValueStorage,
        cryptoEngine: CryptoEngine
    ): BiometricRepository {
        return BiometricRepositoryImpl(
            biometricManager = biometricManager,
            keyValueStorage = keyValueStorage,
            cryptoEngine = cryptoEngine
        )
    }
}