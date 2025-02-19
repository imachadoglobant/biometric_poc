package com.sample.biometric.data

/**
 * User repository
 */
interface UserRepository {

    /**
     * Store user info
     */
    suspend fun saveUser(username: String, token: String)

    /**
     * Store user biometric info
     */
    suspend fun saveBiometricData(biometricToken: String, iv: String)

    /**
     * Retrieve user token if authenticated
     */
    suspend fun getToken(): String

    /**
     * True if biometric info has been previously stored
     */
    suspend fun isTokenPresent(): Boolean

    /**
     * Retrieve user biometric token if enrolled
     */
    suspend fun getBiometricToken(): String

    /**
     * Retrieve user biometric IV if enrolled
     */
    suspend fun getBiometricIv(): String
    /**
     * Retrieve user name if authenticated
     */
    suspend fun getUsername(): String
    /**
     * Clear user session out
     */
    suspend fun logout()
}