package com.sample.biometric.ui.screen.login

import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.AuthenticationCallback
import androidx.biometric.BiometricPrompt.AuthenticationResult
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.sample.biometric.common.findActivity
import timber.log.Timber

@Composable
fun BiometricPromptContainer(
    state: BiometricPromptContainerState,
    onAuthSucceeded: (cryptoObject: BiometricPrompt.CryptoObject?) -> Unit = {},
    onAuthError: (AuthError) -> Unit = {},
) {

    val callback = remember(state) {
        object : AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                Timber.e("onAuthenticationError: $errorCode : $errString")
                state.resetShowFlag()
                onAuthError(AuthError(errorCode, errString.toString()))
            }

            override fun onAuthenticationSucceeded(result: AuthenticationResult) {
                Timber.d("onAuthenticationSucceeded")
                state.resetShowFlag()
                onAuthSucceeded(result.cryptoObject)
            }
        }
    }

    val showPrompt: Boolean by state.isPromptToShow

    if (showPrompt) {
        LocalContext.current.findActivity()?.let { activity ->
            LaunchedEffect(key1 = state.cryptoObject) {
                val prompt = BiometricPrompt(activity, callback)
                prompt.authenticate(state.promptInfo, state.cryptoObject)
            }
        }
    }

}

class BiometricPromptContainerState {
    private lateinit var _cryptoObject: BiometricPrompt.CryptoObject
    private lateinit var _promptInfo: PromptInfo

    val promptInfo: PromptInfo by lazy { _promptInfo }
    val cryptoObject: BiometricPrompt.CryptoObject by lazy {
        _cryptoObject
    }
    private val _isPromptToShow = mutableStateOf(false)
    val isPromptToShow: State<Boolean> = _isPromptToShow

    fun authenticate(promptInfo: PromptInfo, cryptoObject: BiometricPrompt.CryptoObject) {
        _promptInfo = promptInfo
        _cryptoObject = cryptoObject
        _isPromptToShow.value = true
    }

    fun resetShowFlag() {
        _isPromptToShow.value = false
    }
}

@Composable
fun rememberPromptContainerState(): BiometricPromptContainerState = remember {
    BiometricPromptContainerState()
}

data class AuthError(
    val errorCode: Int,
    val errString: String
)