package com.sample.biometric.di.module

import android.content.Context
import androidx.biometric.BiometricManager
import com.sample.biometric.data.crypto.BiometricCryptoEngine
import com.sample.biometric.data.crypto.CryptoEngine
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object SecurityModule {

    @Provides
    fun provideBiometricManager(context: Context) = BiometricManager.from(context)

    @Provides
    @Singleton
    fun provideBiometricCryptoEngine() = BiometricCryptoEngine()

    @Provides
    @Singleton
    fun provideCryptoEngine() = CryptoEngine()

}