package com.sample.biometric.data

import com.sample.biometric.data.model.UserData

/**
 * User repository
 */
interface UserRepository {

    /**
     * Store user info
     */
    suspend fun saveUser(username: String, token: String): UserData

    /**
     * Retrieves user info
     */
    suspend fun getUser(): UserData

    /**
     * Store user biometric info
     */
    suspend fun saveBiometricData(biometricToken: String, iv: String)

    /**
     * True if biometric info has been previously stored
     */
    suspend fun isBiometricTokenPresent(): Boolean

    /**
     * Retrieve user biometric token if enrolled
     */
    suspend fun getBiometricToken(): String

    /**
     * Retrieve user biometric IV if enrolled
     */
    suspend fun getBiometricIv(): String

    /**
     * Remove stored token
     */
    suspend fun expireToken()

    /**
     * Clear user session out
     */
    suspend fun logout()

}