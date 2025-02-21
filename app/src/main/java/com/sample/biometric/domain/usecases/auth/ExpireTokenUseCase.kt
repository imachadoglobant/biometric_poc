package com.sample.biometric.domain.usecases.auth

import com.sample.biometric.data.UserRepository
import com.sample.biometric.data.model.UserData

class ExpireTokenUseCase(private val userRepository: UserRepository) {

    suspend operator fun invoke(user: UserData) {
        userRepository.saveUser(
            user.copy(
                token = "",
                expiredToken = user.token
            )
        )
    }

}