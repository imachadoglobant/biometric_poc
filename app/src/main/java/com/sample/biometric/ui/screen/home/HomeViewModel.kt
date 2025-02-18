package com.sample.biometric.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.biometric.domain.usecases.GetUserUseCase
import com.sample.biometric.domain.usecases.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<HomeUiState> by lazy {
        MutableStateFlow(
            HomeUiState()
        )
    }

    val uiState: StateFlow<HomeUiState> by lazy {
        _uiState.asStateFlow()
    }

    init {
        viewModelScope.launch {
            _uiState.value = uiState.value.copy(
                loggedIn = getUserUseCase().successDataOrNull()?.token?.isNotBlank() == true
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }

}