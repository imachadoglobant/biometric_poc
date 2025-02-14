package com.sample.biometric.ui.screen.login

import androidx.biometric.BiometricPrompt
import com.sample.biometric.common.CryptoPurpose

data class AuthContext(
    val purpose: CryptoPurpose,
    val cryptoObject: BiometricPrompt.CryptoObject
)
