package com.sample.biometric.data

import com.sample.biometric.data.model.UserData

/**
 * User repository
 */
interface UserRepository {

    /**
     * Store user info
     */
    suspend fun saveUser(userData: UserData): UserData?

    /**
     * Retrieves user info
     */
    suspend fun getUser(): UserData?

    /**
     * Clear user info
     */
    suspend fun logout()

}