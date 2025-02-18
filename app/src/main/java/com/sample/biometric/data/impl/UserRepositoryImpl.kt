package com.sample.biometric.data.impl

import com.sample.biometric.common.DataResult
import com.sample.biometric.data.PreferenceRepository
import com.sample.biometric.data.UserRepository
import com.sample.biometric.data.error.InvalidTokenException
import com.sample.biometric.data.model.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import java.util.UUID

class UserRepositoryImpl(private val preferenceRepository: PreferenceRepository) : UserRepository {

    companion object {
        private const val TOKEN_KEY = "TOKEN_KEY"
        private const val USERNAME_KEY = "USERNAME_KEY"
    }

    private val _state: MutableStateFlow<UserData?> by lazy {
        MutableStateFlow(null)
    }

    override val state: StateFlow<UserData?> by lazy {
        _state.asStateFlow()
    }

    override suspend fun login(username: String, password: String) {
        Timber.d("do login")
        val token = UUID.randomUUID().toString()

        preferenceRepository.storeValue(USERNAME_KEY, username)
        preferenceRepository.storeValue(TOKEN_KEY, token)

        _state.value = UserData(
            username = username,
            token = token
        )
    }

    override suspend fun loginWithToken(token: String): DataResult<Unit> {
        val storedToken = preferenceRepository.getValue(TOKEN_KEY)

        if (storedToken != token) {
            logout()
            return DataResult.Error(InvalidTokenException())
        }

        _state.value = UserData(
            username = preferenceRepository.getValue(USERNAME_KEY),
            token = token
        )
        return DataResult.Success(Unit)
    }

    override suspend fun logout() {
        preferenceRepository.clear()
        _state.value = null
    }

}

