package com.sample.biometric.ui.screen.biometric

import android.content.res.Resources
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.AuthenticationCallback
import androidx.biometric.BiometricPrompt.AuthenticationResult
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.sample.biometric.R
import com.sample.biometric.common.findActivity
import com.sample.biometric.data.model.CryptoPurpose
import timber.log.Timber

@Composable
fun BiometricPromptContainer(
    state: BiometricPromptState,
    onAuthSucceeded: (cryptoObject: BiometricPrompt.CryptoObject?) -> Unit = {},
    onAuthError: (BiometricError) -> Unit = {},
) {

    val callback = remember(state) {
        object : AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                Timber.e("onAuthenticationError: $errorCode : $errString")
                state.resetShowFlag()
                onAuthError(BiometricError(errorCode, errString.toString()))
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

@Composable
fun rememberPromptContainerState(): BiometricPromptState = remember {
    BiometricPromptState()
}

fun createPromptInfo(purpose: CryptoPurpose, resources: Resources): PromptInfo {
    return if (purpose == CryptoPurpose.Encryption) {
        PromptInfo.Builder()
            .setTitle(resources.getString(R.string.prompt_title_enroll_token))
            .setSubtitle(resources.getString(R.string.prompt_subtitle_enroll_token))
            .setNegativeButtonText(resources.getString(R.string.prompt_cancel))
            .build()
    } else {
        PromptInfo.Builder()
            .setTitle(resources.getString(R.string.prompt_title_login))
            .setSubtitle(resources.getString(R.string.prompt_subtitle_login))
            .setNegativeButtonText(resources.getString(R.string.prompt_cancel))
            .build()
    }
}

