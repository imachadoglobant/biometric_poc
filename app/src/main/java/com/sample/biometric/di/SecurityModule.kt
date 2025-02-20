package com.sample.biometric.di

import android.content.Context
import androidx.biometric.BiometricManager
import com.sample.biometric.data.crypto.BiometricCryptoEngine
import com.sample.biometric.data.crypto.CryptoManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {

    @Provides
    fun provideBiometricManager(@ApplicationContext context: Context): BiometricManager {
        return BiometricManager.from(context)
    }

    @Provides
    @Singleton
    fun provideCryptoEngine(): BiometricCryptoEngine {
        return BiometricCryptoEngine()
    }

    @Provides
    fun provideCryptoManager(): CryptoManager {
        return CryptoManager()
    }

}