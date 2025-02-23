package com.sample.biometric.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.biometric.domain.usecases.auth.ExpireTokenUseCase
import com.sample.biometric.domain.usecases.auth.GetUserUseCase
import com.sample.biometric.domain.usecases.auth.LogoutUseCase
import com.sample.biometric.ui.ViewState
import com.sample.biometric.ui.modify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val getUser: GetUserUseCase,
    private val expireToken: ExpireTokenUseCase,
    private val logout: LogoutUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<ViewState<HomeUiState>> by lazy {
        MutableStateFlow(
            ViewState.Initial
        )
    }

    val uiState: StateFlow<ViewState<HomeUiState>> by lazy {
        _uiState.asStateFlow()
    }

    fun loadData() = viewModelScope.launch {
        val user = getUser().successDataOrNull()
        Timber.d("user=${user}")

        _uiState.update {
            ViewState.Success(
                HomeUiState(
                    username = user?.username.orEmpty(),
                    loggedIn = user?.token?.isNotBlank() == true
                )
            )
        }
    }

    fun doExpireSession() = viewModelScope.launch {
        expireToken()
        setLoggedOut()
    }


    fun doLogout() = viewModelScope.launch {
        logout()
        setLoggedOut()
    }

    private fun setLoggedOut() {
        _uiState.modify {
            it.copy(
                loggedIn = false
            )
        }
    }

}