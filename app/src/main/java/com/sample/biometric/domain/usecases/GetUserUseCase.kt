package com.sample.biometric.domain.usecases

import com.sample.biometric.data.UserRepository
import com.sample.biometric.data.model.UserData
import com.sample.biometric.domain.DomainResult
import timber.log.Timber

class GetUserUseCase(private val userRepository: UserRepository) {

    suspend operator fun invoke(): DomainResult<UserData> {
        val data = UserData(
            username = userRepository.getUsername(),
            token = userRepository.getToken()
        )
        Timber.d(data.toString())
        return DomainResult.Success(data)
    }

}