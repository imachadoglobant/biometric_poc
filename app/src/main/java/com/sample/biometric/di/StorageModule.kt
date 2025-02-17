package com.sample.biometric.di

import android.content.Context
import com.sample.biometric.data.storage.KeyValueStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    @Singleton
    fun provideKeyValueStorage(
        @ApplicationContext context: Context
    ) = KeyValueStorage(context)
}