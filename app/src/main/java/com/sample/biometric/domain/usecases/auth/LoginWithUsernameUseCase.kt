package com.sample.biometric.domain.usecases.auth

import com.sample.biometric.data.UserRepository
import com.sample.biometric.data.model.UserData
import com.sample.biometric.domain.DomainResult
import timber.log.Timber
import java.util.UUID

class LoginWithUsernameUseCase(private val userRepository: UserRepository) {

    suspend operator fun invoke(username: String, password: String): DomainResult<UserData> {
        Timber.d("do login")
        val token = UUID.randomUUID().toString()

        userRepository.saveUser(
            UserData(
                username = username,
                token = token,
                expiredToken = "",
                biometricToken = "",
                biometricIv = ""
            )
        )

        return DomainResult.Success(
            userRepository.getUser()
        )
    }

}