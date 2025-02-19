package com.sample.biometric.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.biometric.domain.usecases.GetUserUseCase
import com.sample.biometric.domain.usecases.LogoutUseCase
import com.sample.biometric.ui.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<ViewState<HomeUiState>> by lazy {
        MutableStateFlow(
            ViewState.Initial
        )
    }

    val uiState: StateFlow<ViewState<HomeUiState>> by lazy {
        _uiState.asStateFlow()
    }

    fun loadData() {
        viewModelScope.launch {
            val loggedIn = getUserUseCase().successDataOrNull()?.token?.isNotBlank() == true
            Timber.d("loggedIn=${loggedIn}")

            setLoggedIn(loggedIn)
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            setLoggedIn(false)
        }
    }

    private fun setLoggedIn(loggedIn: Boolean) {
        _uiState.update {
            ViewState.Success(
                HomeUiState(
                    loggedIn = loggedIn
                )
            )
        }
    }

}