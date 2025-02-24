package com.sample.biometric.domain.usecases.biometric

import com.sample.biometric.data.repositories.BiometricRepository
import com.sample.biometric.data.repositories.UserRepository
import com.sample.biometric.data.model.BiometricStatus

class GetBiometricStatusUseCase(
    private val biometricRepository: BiometricRepository,
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(): BiometricStatus {
        val user = userRepository.retrieve()
        return biometricRepository.getBiometricStatus(
            isTokenPresent = user?.biometricToken?.isNotBlank() == true
                && user.biometricIv.isNotBlank()
        )
    }

}