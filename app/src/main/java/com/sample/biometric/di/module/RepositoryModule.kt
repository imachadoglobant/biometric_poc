package com.sample.biometric.di.module

import androidx.biometric.BiometricManager
import com.sample.biometric.data.repositories.BiometricRepository
import com.sample.biometric.data.dao.UserDataDao
import com.sample.biometric.data.repositories.UserRepository
import com.sample.biometric.data.crypto.BiometricCryptoEngine
import com.sample.biometric.data.crypto.CryptoEngine
import com.sample.biometric.data.repositories.impl.BiometricRepositoryImpl
import com.sample.biometric.data.repositories.impl.UserRepositoryImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(
        userDataDao: UserDataDao,
        cryptoEngine: CryptoEngine
    ) : UserRepository = UserRepositoryImpl(userDataDao, cryptoEngine)

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