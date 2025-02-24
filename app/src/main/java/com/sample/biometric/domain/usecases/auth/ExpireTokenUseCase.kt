package com.sample.biometric.domain.usecases.auth

import com.sample.biometric.data.repositories.UserRepository
import com.sample.biometric.data.model.UserData

class ExpireTokenUseCase(private val userRepository: UserRepository) {

    suspend operator fun invoke(user: UserData): UserData {
        return userRepository.save(
            user.copy(
                token = "",
                expiredToken = user.token,
            )
        )
    }

}