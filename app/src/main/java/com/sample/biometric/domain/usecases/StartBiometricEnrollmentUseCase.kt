package com.sample.biometric.domain.usecases

import androidx.biometric.BiometricPrompt.CryptoObject
import com.sample.biometric.common.DataResult
import com.sample.biometric.data.BiometricRepository
import com.sample.biometric.domain.DomainResult

class StartBiometricEnrollmentUseCase(private val biometricRepository: BiometricRepository) {

    suspend operator fun invoke(cryptoObject: CryptoObject, token: String): DomainResult<Unit> {
        return when(val result = biometricRepository.storeEncryptedToken(cryptoObject, token)) {
            is DataResult.Error -> DomainResult.Error(result.exception)
            is DataResult.Success -> DomainResult.Success(result.data)
        }
    }

}