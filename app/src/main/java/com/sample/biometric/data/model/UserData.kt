package com.sample.biometric.data.model

data class UserData(
    val id: Long = 0,
    val username: String,
    val token: String,
    val expiredToken: String
)