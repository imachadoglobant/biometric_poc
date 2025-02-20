package com.sample.biometric.ui.screen.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.sample.biometric.R
import com.sample.biometric.ui.ViewState
import com.sample.biometric.ui.navigation.HomeRoute
import com.sample.biometric.ui.navigation.LoginRoute
import com.sample.biometric.ui.retrieveViewModel
import com.sample.biometric.ui.screen.biometric.BiometricPromptContainer
import com.sample.biometric.ui.screen.biometric.createPromptInfo
import com.sample.biometric.ui.screen.biometric.rememberPromptContainerState

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = retrieveViewModel<LoginViewModel>(),
    onUserLoginReady: () -> Unit = {},
) {
    val uiState: ViewState<LoginState> by viewModel.uiState.collectAsStateWithLifecycle()
    val successState = (uiState as? ViewState.Success)?.data
    val navigateToHome by remember(uiState) {
        derivedStateOf { successState?.isAuthenticated == true && !successState.askBiometricEnrollment }
    }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(key1 = Unit) {
        viewModel.loadData()
    }

    if (navigateToHome) {
        LaunchedEffect(key1 = Unit) {
            onUserLoginReady()
        }
    }

    val promptContainerState = rememberPromptContainerState()
    BiometricPromptContainer(
        state = promptContainerState,
        onAuthSucceeded = { cryptoObject ->
            viewModel.onAuthSucceeded(cryptoObject)
        },
        onAuthError = { authErr ->
            viewModel.onAuthError(authErr.errorCode, authErr.errString)
        }
    )

    successState?.biometricContext?.let { auth ->
        val resources = LocalContext.current.resources
        LaunchedEffect(key1 = auth) {
            val promptInfo = createPromptInfo(auth.purpose, resources)
            promptContainerState.authenticate(promptInfo, auth.cryptoObject)
        }
    }

    Column(
        modifier = modifier.padding(top = 120.dp),
        horizontalAlignment = CenterHorizontally,
    ) {
        OutlinedTextField(
            value = successState?.usernameField.orEmpty(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            placeholder = {
                Text(text = stringResource(id = R.string.username_placeholder))
            },
            onValueChange = {
                viewModel.setUsername(it)
            }
        )

        FormSpacer()

        OutlinedTextField(
            value = successState?.passwordField.orEmpty(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus(force = true)
                    viewModel.doLogin()
                }
            ),
            visualTransformation = PasswordVisualTransformation(),
            placeholder = {
                Text(text = stringResource(id = R.string.password_placeholder))
            },
            onValueChange = {
                viewModel.setPassword(it)
            })

        FormSpacer()

        Button(
            onClick = {
                focusManager.clearFocus(force = true)
                viewModel.doLogin()
            }
        ) {
            Text(text = stringResource(id = R.string.login_button_text))
        }

        FormSpacer()

        UseBiometricLoginButton(
            visible = successState?.canLoginWithBiometry == true,
            onClick = {
                viewModel.requireBiometricLogin()
            }
        )
    }
}

@Composable
fun FormSpacer() {
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun UseBiometricLoginButton(
    modifier: Modifier = Modifier,
    visible: Boolean,
    onClick: () -> Unit
) {
    if (visible) {
        OutlinedButton(
            modifier = modifier,
            onClick = onClick
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_fingerprint_24),
                contentDescription = stringResource(id = R.string.button_biometric_content_descr),
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
            Text(text = stringResource(id = R.string.button_biometric_text))

        }
    }
}


fun NavGraphBuilder.addLoginRoute(navController: NavController) {
    composable(
        route = LoginRoute.route
    ) {
        LoginScreen(
            modifier = Modifier.fillMaxSize(),
            onUserLoginReady = {
                navController.navigate(
                    route = HomeRoute.route,
                    navOptions = NavOptions
                        .Builder()
                        .setPopUpTo(LoginRoute.route, true)
                        .build()
                )
            }
        )
    }
}



