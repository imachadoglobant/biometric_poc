package com.sample.biometric.data.entities

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
    val expiredToken: String
)