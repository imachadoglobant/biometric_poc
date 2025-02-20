package com.sample.biometric.di

import android.app.Application
import com.sample.biometric.BiometricApp
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
