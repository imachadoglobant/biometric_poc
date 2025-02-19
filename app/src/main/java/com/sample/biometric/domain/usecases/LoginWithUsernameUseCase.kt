package com.sample.biometric.domain.usecases

import com.sample.biometric.data.UserRepository
import com.sample.biometric.data.model.UserData
import com.sample.biometric.domain.DomainResult
import timber.log.Timber
import java.util.UUID

class LoginWithUsernameUseCase(private val userRepository: UserRepository) {

    suspend operator fun invoke(username: String, password: String): DomainResult<UserData> {
        Timber.d("do login")
        val token = UUID.randomUUID().toString()

        userRepository.saveUser(username, token)

        return DomainResult.Success(
            UserData(
                username = username,
                token = token
            )
        )
    }

}