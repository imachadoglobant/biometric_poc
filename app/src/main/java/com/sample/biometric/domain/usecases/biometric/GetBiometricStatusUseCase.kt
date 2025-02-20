package com.sample.biometric.domain.usecases.biometric

import com.sample.biometric.data.BiometricRepository
import com.sample.biometric.data.UserRepository
import com.sample.biometric.data.model.BiometricStatus

class GetBiometricStatusUseCase(
    private val biometricRepository: BiometricRepository,
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(): BiometricStatus {
        return biometricRepository.getBiometricStatus(userRepository.isTokenPresent())
    }

}