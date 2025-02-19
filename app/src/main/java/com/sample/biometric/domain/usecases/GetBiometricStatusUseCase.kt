package com.sample.biometric.domain.usecases

import com.sample.biometric.data.BiometricRepository
import com.sample.biometric.data.model.BiometricStatus

class GetBiometricStatusUseCase(private val biometricRepository: BiometricRepository) {

    suspend operator fun invoke(): BiometricStatus {
        return biometricRepository.getBiometricStatus()
    }

}