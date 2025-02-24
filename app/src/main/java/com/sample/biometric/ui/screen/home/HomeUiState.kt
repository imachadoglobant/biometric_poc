package com.sample.biometric.ui.screen.home

import com.sample.biometric.data.model.UserData

data class HomeUiState(
    val user: UserData?
) {
    val loggedIn = user?.token?.isNotBlank() == true
}