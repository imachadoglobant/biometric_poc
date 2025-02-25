package com.sample.biometric.di.module

import android.content.Context
import com.sample.biometric.data.AppDatabase
import com.sample.biometric.data.dao.UserDataDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(context: Context) : AppDatabase = AppDatabase(context)

    @Provides
    fun provideUserDataDao(appDatabase: AppDatabase): UserDataDao = appDatabase.userDao()

}