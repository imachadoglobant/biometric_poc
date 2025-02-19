package com.sample.biometric.domain.usecases

import androidx.biometric.BiometricPrompt.CryptoObject
import com.sample.biometric.common.DataResult
import com.sample.biometric.data.BiometricRepository
import com.sample.biometric.domain.DomainResult

class GetBiometricTokenUseCase(private val biometricRepository: BiometricRepository) {

    suspend operator fun invoke(cryptoObject: CryptoObject): DomainResult<String> {
        return when (val result = biometricRepository.decryptToken(cryptoObject)) {
            is DataResult.Error -> DomainResult.Error(result.exception)
            is DataResult.Success -> DomainResult.Success(result.data)
        }
    }

}