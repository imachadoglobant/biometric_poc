package com.sample.biometric.data.impl

import com.sample.biometric.data.UserRepository
import com.sample.biometric.data.storage.KeyValueStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

class UserRepositoryImpl(
    //injected only to perform mock authentication
    private val keyValueStorage: KeyValueStorage
) : UserRepository {

    private val _isUserLoggedIn: MutableStateFlow<Boolean> by lazy {
        MutableStateFlow(false)
    }

    override val isUserLoggedIn: StateFlow<Boolean> by lazy {
        _isUserLoggedIn.asStateFlow()
    }

    override suspend fun login(username: String, password: String) {
        Timber.d("do login")
        _isUserLoggedIn.value = true
    }

    override suspend fun loginWithToken(token: String) {
        val storedToken = keyValueStorage.getValue(BiometricRepositoryImpl.TOKEN_KEY)
        _isUserLoggedIn.value = storedToken == token
    }

    override suspend fun logout() {
        _isUserLoggedIn.value = false
    }
}