package com.sample.biometric.di.module

import android.content.Context
import com.sample.biometric.BiometricApp
import dagger.Binds
import dagger.Module

@Module(
    includes = [
        ActivitiesModule::class
    ]
)
interface BiometricAppModule {

    @Binds
    fun context(application: BiometricApp): Context

}
