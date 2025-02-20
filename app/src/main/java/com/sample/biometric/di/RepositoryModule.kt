package com.sample.biometric.di

import android.content.Context
import androidx.biometric.BiometricManager
import com.sample.biometric.data.BiometricRepository
import com.sample.biometric.data.PreferenceRepository
import com.sample.biometric.data.UserRepository
import com.sample.biometric.data.crypto.BiometricCryptoEngine
import com.sample.biometric.data.crypto.CryptoManager
import com.sample.biometric.data.impl.BiometricRepositoryImpl
import com.sample.biometric.data.impl.PreferenceRepositoryImpl
import com.sample.biometric.data.impl.UserRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun providePreferenceRepository(
        @ApplicationContext context: Context,
        cryptoManager: CryptoManager
    ): PreferenceRepository {
        return PreferenceRepositoryImpl(context, cryptoManager)
    }

    @Provides
    @Singleton
    fun provideUserRepository(preferenceRepository: PreferenceRepository): UserRepository {
        return UserRepositoryImpl(preferenceRepository)
    }

    @Provides
    @Singleton
    fun provideTokenRepository(
        biometricManager: BiometricManager,
        cryptoEngine: BiometricCryptoEngine
    ): BiometricRepository {
        return BiometricRepositoryImpl(
            biometricManager = biometricManager,
            cryptoEngine = cryptoEngine
        )
    }
}