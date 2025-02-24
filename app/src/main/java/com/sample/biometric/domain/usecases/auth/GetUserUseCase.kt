package com.sample.biometric.domain.usecases.auth

import com.sample.biometric.data.UserRepository
import com.sample.biometric.data.model.UserData
import com.sample.biometric.domain.DomainResult

class GetUserUseCase(private val userRepository: UserRepository) {

    suspend operator fun invoke(): DomainResult<UserData> {
        val data = userRepository.getUser() ?: run {
            return DomainResult.Error(NullPointerException("user is null"))
        }
        return DomainResult.Success(data)
    }

}