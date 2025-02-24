package com.sample.biometric.di

import androidx.biometric.BiometricManager
import com.sample.biometric.data.BiometricRepository
import com.sample.biometric.data.UserDataDao
import com.sample.biometric.data.UserRepository
import com.sample.biometric.data.crypto.BiometricCryptoEngine
import com.sample.biometric.data.impl.BiometricRepositoryImpl
import com.sample.biometric.data.impl.UserRepositoryImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(userDataDao: UserDataDao) : UserRepository =
        UserRepositoryImpl(userDataDao)

    @Provides
    @Singleton
    fun provideTokenRepository(
        biometricManager: BiometricManager,
        cryptoEngine: BiometricCryptoEngine
    ): BiometricRepository = BiometricRepositoryImpl(
        biometricManager = biometricManager,
        cryptoEngine = cryptoEngine
    )

}