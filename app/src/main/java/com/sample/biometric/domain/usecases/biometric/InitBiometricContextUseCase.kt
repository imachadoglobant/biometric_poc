package com.sample.biometric.domain.usecases.biometric

import com.sample.biometric.common.DataResult
import com.sample.biometric.data.BiometricRepository
import com.sample.biometric.data.UserRepository
import com.sample.biometric.data.model.CryptoPurpose
import com.sample.biometric.domain.DomainResult
import com.sample.biometric.ui.screen.biometric.BiometricContext

class InitBiometricContextUseCase(
    private val biometricRepository: BiometricRepository,
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(purpose: CryptoPurpose): DomainResult<BiometricContext> {
        return when (val result = biometricRepository.createCryptoObject(
            purpose,
            userRepository.getBiometricIv()
        )) {
            is DataResult.Success -> {
                val cryptoObject = result.data ?: run {
                    return DomainResult.Error(NullPointerException("crypto object is null"))
                }

                DomainResult.Success(
                    BiometricContext(
                        purpose = purpose,
                        cryptoObject = cryptoObject
                    )
                )
            }

            is DataResult.Error -> {
                DomainResult.Error(result.exception)
            }
        }
    }

}