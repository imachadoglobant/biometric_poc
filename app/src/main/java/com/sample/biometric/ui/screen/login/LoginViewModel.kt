package com.sample.biometric.ui.screen.login

import androidx.annotation.StringRes
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.CryptoObject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.biometric.R
import com.sample.biometric.data.error.InvalidCryptoLayerException
import com.sample.biometric.data.model.BiometricStatus
import com.sample.biometric.data.model.CryptoPurpose.Decryption
import com.sample.biometric.data.model.CryptoPurpose.Encryption
import com.sample.biometric.domain.usecases.GetBiometricStatusUseCase
import com.sample.biometric.domain.usecases.GetBiometricTokenUseCase
import com.sample.biometric.domain.usecases.GetUserUseCase
import com.sample.biometric.domain.usecases.InitBiometricContextUseCase
import com.sample.biometric.domain.usecases.LoginWithTokenUseCase
import com.sample.biometric.domain.usecases.LoginWithUsernameUseCase
import com.sample.biometric.domain.usecases.StartBiometricEnrollmentUseCase
import com.sample.biometric.domain.DomainResult
import com.sample.biometric.ui.ViewState
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
    private val initBiometricContext: InitBiometricContextUseCase,
    private val getBiometricStatus: GetBiometricStatusUseCase,
    private val startBiometricEnrollment: StartBiometricEnrollmentUseCase,
    private val getBiometricToken: GetBiometricTokenUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<ViewState<LoginUIState>> =
        MutableStateFlow(ViewState.Initial)
    val uiState: StateFlow<ViewState<LoginUIState>> = _uiState.asStateFlow()

    fun loadData() = viewModelScope.launch {
        if (_uiState.value !is ViewState.Initial) return@launch
        val token = getUser().successDataOrNull()?.token
        Timber.d("token=$token")

        _uiState.update {
            ViewState.Success(
                LoginUIState(
                    token = token
                )
            )
        }
    }

    private fun shouldAskTokenEnrollment(
        isLoggedIn: Boolean,
        biometricStatus: BiometricStatus
    ) = isLoggedIn
        && _uiState.value.successDataOrNull()?.askBiometricEnrollment == false
        && !biometricStatus.biometricTokenPresent
        && biometricStatus.canAskAuthentication()

    private suspend fun startBiometricTokenEnrollment(cryptoObject: CryptoObject) {
        val token = _uiState.value.successDataOrNull()?.token.orEmpty()
        when (val result = startBiometricEnrollment(cryptoObject, token)) {
            is DomainResult.Loading -> {
                // Show loading
            }

            is DomainResult.Error -> {
                result.exception.let { e ->
                    if (e is InvalidCryptoLayerException) {
                        handleInvalidCryptoException(e, false)
                    } else {
                        handleError(e)
                    }
                }
            }

            is DomainResult.Success -> {
                Timber.i("fetchAndStoreEncryptedToken done")
            }
        }
    }

    private suspend fun startLoginWithToken(cryptoObject: CryptoObject) =
        viewModelScope.launch {
            when (val result = getBiometricToken(cryptoObject)) {
                is DomainResult.Loading -> {
                    // Show loading
                }

                is DomainResult.Error -> {
                    // Handle resiliency
                    Timber.e("startLoginWithToken cryptoObject is null")
                    handleError(result.exception)
                }

                is DomainResult.Success -> {
                    val token = result.data ?: run {
                        return@launch handleError(NullPointerException("token is null"))
                    }
                    doLoginWithToken(token)
                }
            }
        }

    private fun doLoginWithToken(token: String) = viewModelScope.launch {
        when (val result = loginWithToken(token)) {
            is DomainResult.Loading -> {
                // Show loading
            }

            is DomainResult.Error -> {
                if (result.exception is InvalidCryptoLayerException) {
                    val state = _uiState.value.successDataOrNull() ?: return@launch
                    _uiState.update {
                        ViewState.Success(
                            state.copy(canLoginWithBiometry = false)
                        )
                    }
                } else {
                    handleError(result.exception)
                }
            }

            is DomainResult.Success -> {
                Timber.d("Login Done")
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
            val state = _uiState.value.successDataOrNull() ?: return
            //update to inform ui that login with biometry is not available
            _uiState.update {
                ViewState.Success(
                    state.copy(canLoginWithBiometry = false)
                )
            }
        }
    }

    private fun showMessage(message: String) {
        SnackbarManager.showMessage(message)
    }

    private fun showMessage(@StringRes messageTextId: Int) {
        SnackbarManager.showMessage(messageTextId)
    }

    private fun initBiometric(token: String?) = viewModelScope.launch {
        val isLoggedIn = token?.isNotBlank() == true
        Timber.d("isUserLoggedIn=$isLoggedIn")
        val biometricStatus = getBiometricStatus()

        val askBiometricEnrollment = shouldAskTokenEnrollment(isLoggedIn, biometricStatus)
        Timber.d("askBiometricEnrollment=$askBiometricEnrollment")
        var authContext: BiometricContext? = null
        Timber.d("canLoginWithBiometry=${biometricStatus.canLoginWithBiometricToken()}")

        // we want to check if enrollment is ok or not
        if (askBiometricEnrollment) {
            when (val result = initBiometricContext(Encryption)) {
                is DomainResult.Loading -> {
                    // Show loading
                }

                is DomainResult.Error -> Timber.e(result.exception)

                is DomainResult.Success -> authContext = result.data
            }
        }
        _uiState.update {
            ViewState.Success(
                LoginUIState(
                    token = token,
                    canLoginWithBiometry = biometricStatus.canLoginWithBiometricToken(),
                    askBiometricEnrollment = askBiometricEnrollment,
                    biometricContext = authContext
                )
            )
        }
    }

    fun setUsername(username: String) {
        val state = _uiState.value.successDataOrNull() ?: return
        _uiState.update {
            ViewState.Success(
                state.copy(usernameField = username)
            )
        }
    }

    fun setPassword(password: String) {
        val state = _uiState.value.successDataOrNull() ?: return
        _uiState.update {
            ViewState.Success(
                state.copy(passwordField = password)
            )
        }
    }

    fun doLogin() {
        val state = _uiState.value.successDataOrNull() ?: return
        val username = state.usernameField
        val password = state.passwordField
        if (username.isBlank() || password.isBlank()) {
            showMessage(R.string.msg_error_username_password_required)
            return
        }
        viewModelScope.launch {
            when (val result = loginWithUsername(username, password)) {
                is DomainResult.Loading -> {
                    // Show loading
                }
                is DomainResult.Error -> {
                    // Handle resiliency
                }
                is DomainResult.Success -> {
                    initBiometric(result.data?.token)
                }
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

        val state = _uiState.value.successDataOrNull() ?: return
        _uiState.update {
            ViewState.Success(
                state.copy(
                    askBiometricEnrollment = false,
                    biometricContext = null
                )
            )
        }
    }

    fun onAuthSucceeded(cryptoObject: CryptoObject?) {
        if (cryptoObject == null) {
            Timber.e("cryptoObject is null")
            return
        }

        Timber.i("On Auth Succeeded $cryptoObject")
        val state = _uiState.value.successDataOrNull() ?: return

        _uiState.update {
            ViewState.Success(
                state.copy(
                    askBiometricEnrollment = false,
                    biometricContext = null
                )
            )
        }

        viewModelScope.launch {
            state.biometricContext?.let { authContext ->
                if (authContext.purpose == Encryption) {
                    startBiometricTokenEnrollment(cryptoObject)
                } else {
                    startLoginWithToken(cryptoObject)
                }
            }
        }
    }

    fun requireBiometricLogin() {
        viewModelScope.launch {
            when (val result = initBiometricContext(Decryption)) {
                is DomainResult.Loading -> {
                    // Show loading
                }

                is DomainResult.Error -> {
                    result.exception?.let { e ->
                        if (e is InvalidCryptoLayerException) {
                            handleInvalidCryptoException(e, true)
                        } else {
                            handleError(e)
                        }
                    }
                }

                is DomainResult.Success -> {
                    val state = (_uiState.value as? ViewState.Success)?.data ?: return@launch

                    _uiState.update {
                        ViewState.Success(
                            state.copy(
                                askBiometricEnrollment = false,
                                biometricContext = result.data
                            )
                        )
                    }
                }
            }
        }
    }

}