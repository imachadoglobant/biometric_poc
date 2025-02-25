package com.sample.biometric.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserDataEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val token: String,
    @ColumnInfo(name = "expired_token")
    val expiredToken: String,
    @ColumnInfo(name = "biometric_token")
    val biometricToken: String,
    @ColumnInfo(name = "biometric_iv")
    val biometricIv: String
)