package com.sample.biometric.domain.usecases.biometric

import androidx.biometric.BiometricPrompt.CryptoObject
import com.sample.biometric.common.DataResult
import com.sample.biometric.data.repositories.BiometricRepository
import com.sample.biometric.data.repositories.UserRepository
import com.sample.biometric.domain.DomainResult

class GetBiometricTokenUseCase(
    private val biometricRepository: BiometricRepository,
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(cryptoObject: CryptoObject): DomainResult<String> {
        val user = userRepository.retrieve()
        return when (val result = biometricRepository.decryptToken(
            cryptoObject,
            user?.biometricToken.orEmpty())
        ) {
            is DataResult.Error -> DomainResult.Error(result.exception)
            is DataResult.Success -> DomainResult.Success(result.data)
        }
    }

}