package com.sample.biometric.domain.usecases.biometric

import androidx.biometric.BiometricPrompt.CryptoObject
import com.sample.biometric.common.DataResult
import com.sample.biometric.data.BiometricRepository
import com.sample.biometric.data.UserRepository
import com.sample.biometric.domain.DomainResult

class GetBiometricTokenUseCase(
    private val biometricRepository: BiometricRepository,
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(cryptoObject: CryptoObject): DomainResult<String> {
        val biometricToken = userRepository.getUser()?.biometricToken ?: run {
            return DomainResult.Error(NullPointerException("user is null"))
        }

        return when (val result = biometricRepository.decryptToken(cryptoObject, biometricToken)) {
            is DataResult.Error -> DomainResult.Error(result.exception)
            is DataResult.Success -> DomainResult.Success(result.data)
        }
    }

}