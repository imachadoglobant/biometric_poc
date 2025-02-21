package com.sample.biometric.domain.usecases.biometric

import androidx.biometric.BiometricPrompt.CryptoObject
import com.sample.biometric.common.DataResult
import com.sample.biometric.data.BiometricRepository
import com.sample.biometric.data.UserRepository
import com.sample.biometric.data.model.UserData
import com.sample.biometric.domain.DomainResult

class SaveBiometricDataUseCase(
    private val biometricRepository: BiometricRepository,
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(cryptoObject: CryptoObject, user: UserData): DomainResult<Unit> {
        return when(val result = biometricRepository.getEncryptedToken(cryptoObject, user.token)) {

            is DataResult.Error -> DomainResult.Error(result.exception)

            is DataResult.Success -> {
                val data = result.data ?: run {
                    return DomainResult.Error(NullPointerException("encrypted data is null"))
                }

                userRepository.saveUser(
                    user.copy(
                        biometricToken = data.data,
                        biometricIv = data.iv
                    )
                )

                DomainResult.Success(Unit)
            }
        }
    }

}