package com.sample.biometric.di

import android.app.Application
import com.sample.biometric.BiometricApp
import com.sample.biometric.di.module.BiometricAppModule
import com.sample.biometric.di.module.DatabaseModule
import com.sample.biometric.di.module.RepositoryModule
import com.sample.biometric.di.module.SecurityModule
import com.sample.biometric.di.module.UseCaseModule
import com.sample.biometric.di.module.ViewModelModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        BiometricAppModule::class,
        DatabaseModule::class,
        RepositoryModule::class,
        SecurityModule::class,
        UseCaseModule::class,
        ViewModelModule::class
    ]
)
interface BiometricAppComponent : AndroidInjector<BiometricApp> {

    fun inject(application: Application)

    @Component.Builder
    interface Builder {

        fun build(): BiometricAppComponent

        @BindsInstance
        fun applicationBind(application: BiometricApp): Builder
    }

}
