package com.sample.biometric.ui.screen.biometric

import androidx.biometric.BiometricPrompt.CryptoObject
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

class BiometricPromptState {
    private lateinit var _cryptoObject: CryptoObject
    private lateinit var _promptInfo: PromptInfo

    private val _isPromptToShow = mutableStateOf(false)
    val isPromptToShow: State<Boolean> = _isPromptToShow

    val promptInfo: PromptInfo by lazy { _promptInfo }
    val cryptoObject: CryptoObject by lazy { _cryptoObject }

    fun authenticate(promptInfo: PromptInfo, cryptoObject: CryptoObject) {
        _promptInfo = promptInfo
        _cryptoObject = cryptoObject
        _isPromptToShow.value = true
    }

    fun resetShowFlag() {
        _isPromptToShow.value = false
    }
}