package com.sample.biometric.di

import com.sample.biometric.data.BiometricRepository
import com.sample.biometric.data.UserRepository
import com.sample.biometric.domain.usecases.GetBiometricStatusUseCase
import com.sample.biometric.domain.usecases.GetBiometricTokenUseCase
import com.sample.biometric.domain.usecases.GetUserUseCase
import com.sample.biometric.domain.usecases.InitBiometricContextUseCase
import com.sample.biometric.domain.usecases.LoginWithTokenUseCase
import com.sample.biometric.domain.usecases.LoginWithUsernameUseCase
import com.sample.biometric.domain.usecases.LogoutUseCase
import com.sample.biometric.domain.usecases.StartBiometricEnrollmentUseCase
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

    @Provides
    @Singleton
    fun provideGetBiometricStatusUseCase(
        biometricRepository: BiometricRepository
    ): GetBiometricStatusUseCase {
        return GetBiometricStatusUseCase(biometricRepository)
    }

    @Provides
    @Singleton
    fun provideGetBiometricTokenUseCase(
        biometricRepository: BiometricRepository
    ): GetBiometricTokenUseCase {
        return GetBiometricTokenUseCase(biometricRepository)
    }

    @Provides
    @Singleton
    fun provideInitBiometricContextUseCase(
        biometricRepository: BiometricRepository
    ): InitBiometricContextUseCase {
        return InitBiometricContextUseCase(biometricRepository)
    }

    @Provides
    @Singleton
    fun provideStartBiometricEnrollmentUseCase(
        biometricRepository: BiometricRepository
    ): StartBiometricEnrollmentUseCase {
        return StartBiometricEnrollmentUseCase(biometricRepository)
    }


}