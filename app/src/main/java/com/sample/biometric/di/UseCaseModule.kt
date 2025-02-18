package com.sample.biometric.di

import com.sample.biometric.data.UserRepository
import com.sample.biometric.domain.usecases.GetUserUseCase
import com.sample.biometric.domain.usecases.LoginWithTokenUseCase
import com.sample.biometric.domain.usecases.LoginWithUsernameUseCase
import com.sample.biometric.domain.usecases.LogoutUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetUserUseCase(userRepository: UserRepository): GetUserUseCase {
        return GetUserUseCase(userRepository)
    }

    @Provides
    @Singleton
    fun provideLoginWithUsernameUseCase(userRepository: UserRepository): LoginWithUsernameUseCase {
        return LoginWithUsernameUseCase(userRepository)
    }

    @Provides
    @Singleton
    fun provideLoginWithTokenUseCase(userRepository: UserRepository): LoginWithTokenUseCase {
        return LoginWithTokenUseCase(userRepository)
    }

    @Provides
    @Singleton
    fun provideLogoutUseCase(userRepository: UserRepository): LogoutUseCase {
        return LogoutUseCase(userRepository)
    }

}