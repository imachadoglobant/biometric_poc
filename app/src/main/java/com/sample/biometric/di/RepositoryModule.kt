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
import javax.inject.Singleton

@Module
object RepositoryModule {

    @Provides
    @Singleton
    fun providePreferenceRepository(
        context: Context,
        cryptoManager: CryptoManager
    ) : PreferenceRepository = PreferenceRepositoryImpl(context, cryptoManager)

    @Provides
    @Singleton
    fun provideUserRepository(context: Context) : UserRepository = UserRepositoryImpl(context)

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