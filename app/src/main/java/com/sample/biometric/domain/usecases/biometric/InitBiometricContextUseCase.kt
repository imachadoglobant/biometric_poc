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
        val biometricIv = userRepository.getUser()?.biometricIv ?: run {
            return DomainResult.Error(NullPointerException("biometricIv is null"))
        }

        return when (val result = biometricRepository.createCryptoObject(purpose, biometricIv)) {

            is DataResult.Success -> {
                result.data?.let { cryptoObject ->
                    DomainResult.Success(
                        BiometricContext(
                            purpose = purpose,
                            cryptoObject = cryptoObject
                        )
                    )
                } ?: DomainResult.Error(NullPointerException("crypto object is null"))
            }

            is DataResult.Error -> {
                DomainResult.Error(result.exception)
            }

        }
    }

}