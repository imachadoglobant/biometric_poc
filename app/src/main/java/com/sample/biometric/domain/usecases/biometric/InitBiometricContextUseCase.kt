package com.sample.biometric.domain.usecases.biometric

import com.sample.biometric.common.DataResult
import com.sample.biometric.data.repositories.BiometricRepository
import com.sample.biometric.data.repositories.UserRepository
import com.sample.biometric.data.models.CryptoPurpose
import com.sample.biometric.domain.DomainResult
import com.sample.biometric.ui.screen.biometric.BiometricContext

class InitBiometricContextUseCase(
    private val biometricRepository: BiometricRepository,
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(purpose: CryptoPurpose): DomainResult<BiometricContext> {
        val user = userRepository.retrieve()
        return when (val result = biometricRepository.createCryptoObject(
            purpose,
            user?.biometricIv.orEmpty()
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