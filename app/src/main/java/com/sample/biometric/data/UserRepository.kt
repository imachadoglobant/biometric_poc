package com.sample.biometric.data
import com.sample.biometric.common.DataResult
import com.sample.biometric.data.model.UserData
import kotlinx.coroutines.flow.StateFlow

/**
 * User repository
 */
interface UserRepository {

    /**
     * Flow that contains if the user data when logged in
     */
    val state: StateFlow<UserData?>

    /**
     * Authenticate using username and password
     */
    suspend fun login(username: String, password: String)

    /**
     * Authenticate using biometric data
     */
    suspend fun loginWithToken(token: String): DataResult<Unit>

    /**
     * Clear current session out
     */
    suspend fun logout()
}