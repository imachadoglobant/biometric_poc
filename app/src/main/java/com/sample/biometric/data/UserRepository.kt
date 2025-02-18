package com.sample.biometric.data

/**
 * User repository
 */
interface UserRepository {

    /**
     * Stores user info
     */
    suspend fun saveUser(username: String, token: String)

    /**
     * Retrieve user token if authenticated
     */
    suspend fun getToken(): String

    /**
     * Retrieve user name if authenticated
     */
    suspend fun getUsername(): String

    /**
     * Clears user session out
     */
    suspend fun logout()

}