package com.sample.biometric.data.model

data class UserData(
    val username: String,
    val token: String,
    val expiredToken: String,
    val biometricToken: String,
    val biometricIv: String
) {
    override fun toString(): String {
        return "username=$username, " +
            "token=${token.isNotBlank()}, " +
            "expiredToken=${expiredToken.isNotBlank()}, " +
            "biometricToken=${biometricToken.isNotBlank()}, " +
            "biometricIv=${biometricIv.isNotBlank()}"
    }
}