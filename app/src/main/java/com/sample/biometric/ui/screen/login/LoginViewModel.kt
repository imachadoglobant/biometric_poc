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
import com.sample.biometric.data.model.UserData
import com.sample.biometric.domain.DomainResult
import com.sample.biometric.domain.usecases.auth.GetUserUseCase
import com.sample.biometric.domain.usecases.auth.LoginWithTokenUseCase
import com.sample.biometric.domain.usecases.auth.LoginWithUsernameUseCase
import com.sample.biometric.domain.usecases.biometric.GetBiometricStatusUseCase
import com.sample.biometric.domain.usecases.biometric.GetBiometricTokenUseCase
import com.sample.biometric.domain.usecases.biometric.InitBiometricContextUseCase
import com.sample.biometric.domain.usecases.biometric.SaveBiometricDataUseCase
import com.sample.biometric.ui.ViewState
import com.sample.biometric.ui.modify
import com.sample.biometric.ui.screen.biometric.BiometricContext
import com.sample.biometric.ui.snackbar.SnackbarManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val getUser: GetUserUseCase,
    private val loginWithUsername: LoginWithUsernameUseCase,
    private val loginWithToken: LoginWithTokenUseCase,
    private val initBiometricContext: InitBiometricContextUseCase,
    private val getBiometricStatus: GetBiometricStatusUseCase,
    private val saveBiometricData: SaveBiometricDataUseCase,
    private val getBiometricToken: GetBiometricTokenUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<ViewState<LoginState>> =
        MutableStateFlow(ViewState.Initial)
    val uiState: StateFlow<ViewState<LoginState>> = _uiState.asStateFlow()

    fun loadData() = viewModelScope.launch {
        if (_uiState.value !is ViewState.Initial) return@launch
        val user = getUser().successDataOrNull()

        _uiState.update {
            ViewState.Success(
                LoginState(
                    usernameField = user?.username.orEmpty()
                )
            )
        }

        initBiometric(user)
    }

    private fun shouldAskTokenEnrollment(
        isLoggedIn: Boolean,
        biometricStatus: BiometricStatus
    ) = isLoggedIn
        && _uiState.value.successDataOrNull()?.askBiometricEnrollment == false
        && !biometricStatus.biometricTokenPresent
        && biometricStatus.canAskAuthentication()

    private suspend fun startBiometricTokenEnrollment(cryptoObject: CryptoObject) {
        val user = _uiState.value.successDataOrNull()?.user ?: run {
            Timber.e(NullPointerException("user is null"))
            return
        }
        when (val result = saveBiometricData(cryptoObject, user)) {
            is DomainResult.Loading -> {
                // Show loading
                Timber.d("Biometric enrollment started")
            }

            is DomainResult.Error -> {
                result.exception.let { e ->
                    if (e is InvalidCryptoLayerException) {
                        Timber.d("Biometric enrollment error: InvalidCryptoLayerException")
                        handleInvalidCryptoException(e, false)
                    } else {
                        handleError(e)
                    }
                }
            }

            is DomainResult.Success -> {
                _uiState.modify {
                    it.copy(
                        askBiometricEnrollment = false,
                        biometricContext = null
                    )
                }
                Timber.i("fetchAndStoreEncryptedToken done")
            }
        }
    }

    private suspend fun startLoginWithToken(cryptoObject: CryptoObject) =
        viewModelScope.launch {
            when (val result = getBiometricToken(cryptoObject)) {
                is DomainResult.Loading -> {
                    // Show loading
                    Timber.d("Login with biometric token started")
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
                    Timber.d("Login with biometric token successful")
                    doLoginWithToken(token)
                }
            }
        }

    private fun doLoginWithToken(token: String) = viewModelScope.launch {
        when (val result = loginWithToken(token)) {
            is DomainResult.Loading -> {
                // Show loading
                Timber.d("Login with token started")
            }

            is DomainResult.Error -> {
                if (result.exception is InvalidCryptoLayerException) {
                    Timber.d("Login with token error: InvalidCryptoLayerException")
                    _uiState.modify {
                        it.copy(canLoginWithBiometry = false)
                    }
                } else {
                    handleError(result.exception)
                }
            }

            is DomainResult.Success -> {
                Timber.d("Login Done")
                _uiState.modify {
                    it.copy(
                        user = result.data,
                        askBiometricEnrollment = false,
                        biometricContext = null
                    )
                }
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
            // Update to inform ui that login with biometry is not available
            Timber.d("Login with biometry unavailable")
            _uiState.modify {
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

    private fun initBiometric(user: UserData?) = viewModelScope.launch {
        val isLoggedIn = user?.token?.isNotBlank() == true
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
                    Timber.d("Biometric context initialization started")
                }

                is DomainResult.Error -> {
                    Timber.e(result.exception)
                }

                is DomainResult.Success -> {
                    Timber.d("Biometric context initialized")
                    authContext = result.data
                }
            }
        }
        _uiState.update {
            val state = _uiState.value.successDataOrNull() ?: LoginState()
            ViewState.Success(
                state.copy(
                    user = user,
                    canLoginWithBiometry = biometricStatus.canLoginWithBiometricToken(),
                    askBiometricEnrollment = askBiometricEnrollment,
                    biometricContext = authContext
                )
            )
        }
    }

    fun setUsername(username: String) {
        _uiState.modify {
            it.copy(usernameField = username)
        }
    }

    fun setPassword(password: String) {
        _uiState.modify {
            it.copy(passwordField = password)
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
                    Timber.d("Username and password login started")
                }

                is DomainResult.Error -> {
                    // Handle resiliency
                }

                is DomainResult.Success -> {
                    Timber.d("Username and password login successful")
                    initBiometric(result.data)
                }
            }

        }
    }

    fun onAuthError(errorCode: Int, errorString: String) {
        when (errorCode) {
            BiometricPrompt.ERROR_USER_CANCELED,
            BiometricPrompt.ERROR_CANCELED,
            BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
                Timber.i("operation is cancelled by user interaction")
            }

            else -> {
                Timber.d("Authorization error=$errorCode")
                showMessage(errorString)
            }
        }

        _uiState.modify {
            it.copy(
                askBiometricEnrollment = false,
                biometricContext = null
            )
        }
    }

    fun onAuthSucceeded(cryptoObject: CryptoObject?) {
        if (cryptoObject == null) {
            Timber.e("cryptoObject is null")
            return
        }

        Timber.i("On Auth Succeeded")

        viewModelScope.launch {
            val state = _uiState.value.successDataOrNull() ?: return@launch
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
                    Timber.d("Biometric context initialization started")
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
                    Timber.d("Biometric context initialized")
                    _uiState.modify {
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