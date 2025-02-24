package com.sample.biometric.data.repositories

import com.sample.biometric.data.model.UserData

/**
 * User repository
 */
interface UserRepository {

    /**
     * Store user info
     */
    suspend fun save(user: UserData): UserData

    /**
     * Retrieves user info
     */
    suspend fun retrieve(): UserData?

    /**
     * Clear user session out
     */
    suspend fun logout()

}