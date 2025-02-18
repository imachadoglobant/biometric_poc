package com.sample.biometric.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.biometric.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<HomeUiState> by lazy {
        MutableStateFlow(
            HomeUiState(
                loggedIn = userRepository.state.value != null
            )
        )
    }

    val uiState: StateFlow<HomeUiState> by lazy {
        _uiState.asStateFlow()
    }

    init {
        viewModelScope.launch {
            userRepository.state.collect {
                _uiState.value = uiState.value.copy(
                    loggedIn = it != null
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }

}