package com.sample.biometric

import com.sample.biometric.di.DaggerBiometricAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import timber.log.Timber

class BiometricApp: DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerBiometricAppComponent.builder()
            .applicationBind(this)
            .build().apply {
                inject(this@BiometricApp)
            }
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

}