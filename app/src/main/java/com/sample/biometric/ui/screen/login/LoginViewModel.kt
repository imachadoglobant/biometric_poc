package com.sample.biometric.ui.screen.login

import androidx.annotation.StringRes
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.CryptoObject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.biometric.R
import com.sample.biometric.common.DataResult
import com.sample.biometric.common.DataResult.Error
import com.sample.biometric.common.DataResult.Loading
import com.sample.biometric.common.DataResult.Success
import com.sample.biometric.data.BiometricRepository
import com.sample.biometric.data.error.InvalidCryptoLayerException
import com.sample.biometric.data.model.BiometricInfo
import com.sample.biometric.data.model.CryptoPurpose
import com.sample.biometric.data.model.CryptoPurpose.Decryption
import com.sample.biometric.domain.usecases.GetUserUseCase
import com.sample.biometric.domain.usecases.LoginWithTokenUseCase
import com.sample.biometric.domain.usecases.LoginWithUsernameUseCase
import com.sample.biometric.ui.screen.biometric.BiometricContext
import com.sample.biometric.ui.snackbar.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val getUser: GetUserUseCase,
    private val loginWithUsername: LoginWithUsernameUseCase,
    private val loginWithToken: LoginWithTokenUseCase,
    private val biometricRepository: BiometricRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<LoginUIState> = MutableStateFlow(LoginUIState())
    val uiState: StateFlow<LoginUIState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            initState()
        }
    }

    private suspend fun initState() {
        val token = getUser().successDataOrNull()?.token
        val isLoggedIn = token?.isNotBlank() == true
        Timber.d("isUserLoggedIn=$isLoggedIn")
        val biometricInfo = biometricRepository.getBiometricInfo()

        val currentState = uiState.value
        val askBiometricEnrollment =
            shouldAskTokenEnrollment(isLoggedIn, currentState, biometricInfo)
        var authContext = currentState.biometricContext

        // we want to check if enrollment is ok or not
        if (askBiometricEnrollment) {
            when (val result = prepareAuthContext(CryptoPurpose.Encryption)) {
                is Loading -> {
                    // Show loading
                }
                is Error -> Timber.e(result.exception)
                is Success -> authContext = result.data
            }
        }
        _uiState.update {
            it.copy(
                token = token,
                canLoginWithBiometry = canLoginWithBiometricToken(biometricInfo),
                askBiometricEnrollment = askBiometricEnrollment,
                biometricContext = authContext
            )
        }
    }

    private fun canLoginWithBiometricToken(biometricInfo: BiometricInfo) =
        biometricInfo.biometricTokenPresent && biometricInfo.canAskAuthentication()

    private fun shouldAskTokenEnrollment(
        isLoggedIn: Boolean,
        currentState: LoginUIState,
        biometricInfo: BiometricInfo
    ) = isLoggedIn
        && !currentState.askBiometricEnrollment
        && !biometricInfo.biometricTokenPresent
        && biometricInfo.canAskAuthentication()

    private suspend fun startBiometricTokenEnrollment(cryptoObject: CryptoObject) {
        val token = _uiState.value.token.orEmpty()
        when (val result = biometricRepository.fetchAndStoreEncryptedToken(cryptoObject, token)) {
            is Loading -> {
                // Show progress
            }
            is Error -> {
                result.exception?.let { e ->
                    if (e is InvalidCryptoLayerException) {
                        handleInvalidCryptoException(e, false)
                    } else {
                        handleError(e)
                    }
                }
            }
            is Success -> {
                Timber.i("fetchAndStoreEncryptedToken done")
            }
        }
    }

    private suspend fun startLoginWithToken(cryptoObject: CryptoObject) {
        val tokenAsCredential =
            (biometricRepository.decryptToken(cryptoObject) as? Success?)?.data ?: run {
                // Handle resiliency
                Timber.e("startLoginWithToken cryptoObject is null")
                return
            }
        viewModelScope.launch {
            when (val result = loginWithToken(tokenAsCredential)) {
                is Loading -> {
                    // Show progress indicator
                }

                is Error -> {
                    if (result.exception is InvalidCryptoLayerException) {
                        _uiState.update { it.copy(canLoginWithBiometry = false) }
                    } else {
                        handleError(result.exception)
                    }
                }

                is Success -> {
                    Timber.d("Login Done")
                }
            }
        }
    }

    private suspend fun prepareAuthContext(purpose: CryptoPurpose): DataResult<BiometricContext> {
        return when (val result = biometricRepository.createCryptoObject(purpose)) {
            is Success -> {
                val cryptoObject = result.data ?: return Error()
                Success(
                    BiometricContext(
                        purpose = purpose,
                        cryptoObject = cryptoObject
                    )
                )
            }

            else -> {
                Error((result as? Error)?.exception)
            }
        }
    }

    private fun handleError(e: Throwable?) {
        Timber.e(e, "handleException: ${e?.message}")
        e?.let {
            SnackbarManager.showMessage(R.string.msg_error_generic)
        }
    }

    private fun handleInvalidCryptoException(
        e: InvalidCryptoLayerException,
        isLogin: Boolean
    ) {
        Timber.e(e, "handleInvalidCryptoException... isLogin: $isLogin")
        if (e.isKeyPermanentlyInvalidated) {
            SnackbarManager.showMessage(R.string.msg_error_key_permanently_invalidated)
        } else if (e.isKeyInitFailed) {
            SnackbarManager.showMessage(R.string.msg_error_key_init_fail)
        } else {
            SnackbarManager.showMessage(R.string.msg_error_generic)
        }
        if (isLogin) {
            //update to inform ui that login with biometry is not available
            _uiState.update {
                it.copy(canLoginWithBiometry = false)
            }
        }
    }

    private fun showMessage(message: String) {
        SnackbarManager.showMessage(message)
    }

    private fun showMessage(@StringRes messageTextId: Int) {
        SnackbarManager.showMessage(messageTextId)
    }

    fun setUsername(username: String) {
        _uiState.value = _uiState.value.copy(usernameField = username)
    }

    fun setPassword(password: String) {
        _uiState.value = _uiState.value.copy(passwordField = password)
    }

    fun doLogin() {
        val username = _uiState.value.usernameField
        val password = _uiState.value.passwordField
        if (username.isBlank() || password.isBlank()) {
            showMessage(R.string.msg_error_username_password_required)
            return
        }
        viewModelScope.launch {
            val result = loginWithUsername(username, password)
            _uiState.update {
                it.copy(token = result.token)
            }
        }
    }

    fun onAuthError(errorCode: Int, errString: String) {
        when (errorCode) {
            BiometricPrompt.ERROR_USER_CANCELED,
            BiometricPrompt.ERROR_CANCELED,
            BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
                Timber.i("operation is cancelled by user interaction")
            }

            else -> {
                showMessage(errString)
            }
        }
        _uiState.update { it.copy(askBiometricEnrollment = false, biometricContext = null) }
    }

    fun onAuthSucceeded(cryptoObject: CryptoObject?) {
        Timber.i("On Auth Succeeded $cryptoObject")
        val pendingAuthContext = uiState.value.biometricContext
        _uiState.update {
            it.copy(
                askBiometricEnrollment = false,
                biometricContext = null
            )
        }
        viewModelScope.launch {
            pendingAuthContext?.let { authContext ->
                if (cryptoObject == null) {
                    Timber.e("cryptoObject is null")
                    return@launch
                }
                if (authContext.purpose == CryptoPurpose.Encryption) {
                    startBiometricTokenEnrollment(cryptoObject)
                } else {
                    startLoginWithToken(cryptoObject)
                }
            }

        }
    }

    fun requireBiometricLogin() {
        viewModelScope.launch {
            when (val result = prepareAuthContext(Decryption)) {
                is Loading -> {
                    // Show progress
                }
                is Error -> {
                    result.exception?.let { e ->
                        if (e is InvalidCryptoLayerException) {
                            handleInvalidCryptoException(e, true)
                        } else {
                            handleError(e)
                        }
                    }
                }
                is Success -> {
                    _uiState.update {
                        it.copy(
                            askBiometricEnrollment = false,
                            biometricContext = result.data
                        )
                    }
                }
            }
        }
    }

}