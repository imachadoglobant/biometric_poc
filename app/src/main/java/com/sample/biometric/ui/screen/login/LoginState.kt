package com.sample.biometric.ui.screen.login

import com.sample.biometric.data.model.UserData
import com.sample.biometric.ui.screen.biometric.BiometricContext

data class LoginState (
    val usernameField: String = "",
    val passwordField: String = "",

    /**
     * True when we want to render the "access with biometry" button
     */
    val canLoginWithBiometry: Boolean = false,

    /**
     * User data when logged in, null otherwise
     */
    val user: UserData? = null,

    /**
     * Indicate that we should to show the biometric prompt to the user to enroll
     * the biometric token
     */
    val askBiometricEnrollment: Boolean = false,

    /**
     * Represent the Authentication context of our prompt
     */
    val biometricContext: BiometricContext? = null,
) {

    val isAuthenticated = user?.token?.isNotEmpty() == true

}

