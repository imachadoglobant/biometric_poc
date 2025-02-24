package com.sample.biometric.di

import android.content.Context
import androidx.biometric.BiometricManager
import com.sample.biometric.data.crypto.BiometricCryptoEngine
import com.sample.biometric.data.crypto.CryptoManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object SecurityModule {

    @Provides
    fun provideBiometricManager(context: Context) = BiometricManager.from(context)

    @Provides
    @Singleton
    fun provideCryptoEngine() = BiometricCryptoEngine()

    @Provides
    @Singleton
    fun provideCryptoManager() = CryptoManager()

}