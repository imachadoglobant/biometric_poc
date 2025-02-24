package com.sample.biometric.di

import com.sample.biometric.data.repositories.BiometricRepository
import com.sample.biometric.data.repositories.UserRepository
import com.sample.biometric.domain.usecases.auth.ExpireTokenUseCase
import com.sample.biometric.domain.usecases.auth.GetUserUseCase
import com.sample.biometric.domain.usecases.auth.LoginWithTokenUseCase
import com.sample.biometric.domain.usecases.auth.LoginWithUsernameUseCase
import com.sample.biometric.domain.usecases.auth.LogoutUseCase
import com.sample.biometric.domain.usecases.biometric.GetBiometricStatusUseCase
import com.sample.biometric.domain.usecases.biometric.GetBiometricTokenUseCase
import com.sample.biometric.domain.usecases.biometric.InitBiometricContextUseCase
import com.sample.biometric.domain.usecases.biometric.SaveBiometricDataUseCase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetUserUseCase(userRepository: UserRepository) =
        GetUserUseCase(userRepository)

    @Provides
    @Singleton
    fun provideLoginWithUsernameUseCase(userRepository: UserRepository) =
        LoginWithUsernameUseCase(userRepository)

    @Provides
    @Singleton
    fun provideLoginWithTokenUseCase(userRepository: UserRepository) =
        LoginWithTokenUseCase(userRepository)

    @Provides
    @Singleton
    fun provideExpireTokenUseCase(userRepository: UserRepository) =
        ExpireTokenUseCase(userRepository)

    @Provides
    @Singleton
    fun provideLogoutUseCase(
        biometricRepository: BiometricRepository,
        userRepository: UserRepository
    ) = LogoutUseCase(biometricRepository, userRepository)

    @Provides
    @Singleton
    fun provideGetBiometricStatusUseCase(
        biometricRepository: BiometricRepository,
        userRepository: UserRepository
    ) = GetBiometricStatusUseCase(biometricRepository, userRepository)

    @Provides
    @Singleton
    fun provideGetBiometricTokenUseCase(
        biometricRepository: BiometricRepository,
        userRepository: UserRepository
    ) = GetBiometricTokenUseCase(biometricRepository, userRepository)

    @Provides
    @Singleton
    fun provideInitBiometricContextUseCase(
        biometricRepository: BiometricRepository,
        userRepository: UserRepository
    ) = InitBiometricContextUseCase(biometricRepository, userRepository)

    @Provides
    @Singleton
    fun provideSaveBiometricDataUseCase(
        biometricRepository: BiometricRepository,
        userRepository: UserRepository
    ) = SaveBiometricDataUseCase(biometricRepository, userRepository)

}