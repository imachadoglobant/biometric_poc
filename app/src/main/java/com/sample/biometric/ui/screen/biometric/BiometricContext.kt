package com.sample.biometric.ui.screen.biometric

import androidx.biometric.BiometricPrompt
import com.sample.biometric.data.model.CryptoPurpose

data class BiometricContext(
    val purpose: CryptoPurpose,
    val cryptoObject: BiometricPrompt.CryptoObject
)
