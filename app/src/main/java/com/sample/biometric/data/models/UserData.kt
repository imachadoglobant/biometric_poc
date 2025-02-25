package com.sample.biometric.data.models

data class UserData(
    val id: Long = 0,
    val username: String,
    val token: String,
    val expiredToken: String,
    val biometricToken: String,
    val biometricIv: String
) {
    override fun toString(): String {
        return "UserData(" +
            "id=$id, " +
            "username=$username, " +
            "token=${token.isNotBlank()}, " +
            "expiredToken=${expiredToken.isNotBlank()}, " +
            "biometricToken=${biometricToken.isNotBlank()}, " +
            "biometricIv=${biometricIv.isNotBlank()}" +
            ")"
    }
}