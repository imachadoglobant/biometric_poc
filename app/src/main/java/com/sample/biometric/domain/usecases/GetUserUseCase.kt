package com.sample.biometric.domain.usecases

import com.sample.biometric.common.DataResult
import com.sample.biometric.data.UserRepository
import com.sample.biometric.data.model.UserData

class GetUserUseCase(private val userRepository: UserRepository) {

    suspend operator fun invoke(): DataResult<UserData> {
        return DataResult.Success(
            UserData(
                username = userRepository.getUsername(),
                token = userRepository.getToken()
            )
        )
    }

}