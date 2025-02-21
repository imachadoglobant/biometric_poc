package com.sample.biometric.domain.usecases.biometric

import com.sample.biometric.data.BiometricRepository
import com.sample.biometric.data.UserRepository
import com.sample.biometric.data.model.BiometricStatus

class GetBiometricStatusUseCase(
    private val biometricRepository: BiometricRepository,
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(): BiometricStatus {
        val user = userRepository.getUser() ?: run {
            return biometricRepository.getBiometricStatus(false)
        }
        return biometricRepository.getBiometricStatus(
            isTokenPresent = user.biometricToken.isNotBlank() && user.biometricIv.isNotBlank()
        )
    }

}