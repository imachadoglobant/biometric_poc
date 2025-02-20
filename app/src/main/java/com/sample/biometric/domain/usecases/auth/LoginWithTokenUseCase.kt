package com.sample.biometric.domain.usecases.auth

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
        var user = userRepository.getUser()

        if (user.expiredToken != token) {
            userRepository.logout()
            return DomainResult.Error(InvalidTokenException())
        }

        // Token should be refreshed here
        user = userRepository.saveUser(user.username, token)

        return DomainResult.Success(user)
    }

}