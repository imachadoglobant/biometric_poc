package com.sample.biometric.domain.usecases

import com.sample.biometric.data.UserRepository
import com.sample.biometric.data.model.UserData
import timber.log.Timber
import java.util.UUID

class LoginWithUsernameUseCase(private val userRepository: UserRepository) {

    suspend operator fun invoke(username: String, password: String): UserData {
        Timber.d("do login")
        val token = UUID.randomUUID().toString()

        userRepository.saveUser(username, token)

        return UserData(
            username = username,
            token = token
        )
    }

}