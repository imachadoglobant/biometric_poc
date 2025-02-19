package com.sample.biometric.domain.usecases

import com.sample.biometric.data.UserRepository
import com.sample.biometric.data.error.InvalidTokenException
import com.sample.biometric.data.model.UserData
import com.sample.biometric.domain.DomainResult
import kotlinx.coroutines.delay

class LoginWithTokenUseCase(private val userRepository: UserRepository) {

    companion object {
        private const val DELAY = 100L
    }

    suspend operator fun invoke(token: String): DomainResult<UserData> {
        delay(DELAY)
        val storedToken = userRepository.getToken()

        if (storedToken != token) {
            userRepository.logout()
            return DomainResult.Error(InvalidTokenException())
        }

        return DomainResult.Success(
            UserData(
                username = userRepository.getUsername(),
                token = token
            )
        )
    }

}