package com.sample.biometric.di.module

import com.sample.biometric.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivitiesModule {
    @ContributesAndroidInjector
    abstract fun contributesMainActivity(): MainActivity
}
