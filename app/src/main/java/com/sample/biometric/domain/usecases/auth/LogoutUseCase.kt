package com.sample.biometric.domain.usecases.auth

import com.sample.biometric.data.repositories.BiometricRepository
import com.sample.biometric.data.repositories.UserRepository

class LogoutUseCase(
    private val biometricRepository: BiometricRepository,
    private val userRepository: UserRepository
) {

    suspend operator fun invoke() {
        userRepository.logout()
        biometricRepository.clear()
    }

}