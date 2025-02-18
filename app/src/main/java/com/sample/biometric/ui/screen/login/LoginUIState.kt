package com.sample.biometric.ui.screen.login

data class LoginUIState (
    val usernameField: String = "",
    val passwordField: String = "",

    /**
     * True when we want to render the "access with biometry" button
     */
    val canLoginWithBiometry: Boolean = false,

    /**
     * User token when the user is logged in, null otherwise
     */
    val token: String? = null,

    /**
     * Indicate that we should to show the biometric prompt to the user to enroll
     * the biometric token
     */
    val askBiometricEnrollment: Boolean = false,

    /**
     * Represent the Authentication context of our prompt
     */
    val authContext: AuthContext? = null,
) {

    val isAuthenticated = token != null

}

