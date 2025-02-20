package com.sample.biometric.domain.usecases.auth

import com.sample.biometric.data.UserRepository

class LogoutUseCase(private val userRepository: UserRepository) {

    suspend operator fun invoke() {
        userRepository.logout()
    }

}