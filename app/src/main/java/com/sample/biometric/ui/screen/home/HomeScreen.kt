package com.sample.biometric.ui.screen.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.sample.biometric.R
import com.sample.biometric.ui.ViewState
import com.sample.biometric.ui.navigation.HomeRoute
import com.sample.biometric.ui.navigation.LoginRoute
import com.sample.biometric.ui.retrieveViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = retrieveViewModel<HomeViewModel>(),
    onLogoutDone: () -> Unit = {}
) {

    val uiState: ViewState<HomeUiState> by viewModel.uiState.collectAsStateWithLifecycle()
    val successState = (uiState as? ViewState.Success)?.data

    LaunchedEffect(key1 = Unit) {
        viewModel.loadData()
    }

    if (successState?.loggedIn == false) {
        LaunchedEffect(key1 = Unit) {
            onLogoutDone()
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(top = 120.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(
            R.string.home_content_message,
            successState?.user?.username.orEmpty()
        ))
        Divider(Modifier.width(8.dp))
        Button(
            onClick = {
                viewModel.doExpireSession()
            }
        ) {
            Text(text = stringResource(id = R.string.expire_button_text))
        }
        Button(
            onClick = {
                viewModel.doLogout()
            }
        ) {
            Text(text = stringResource(id = R.string.logout_button_text))
        }
    }
}

fun NavGraphBuilder.addHomeRoute(navController: NavHostController) {
    composable(
        route = HomeRoute.route
    ) {
        HomeScreen(
            onLogoutDone = {
                navController.navigate(
                    route = LoginRoute.route,
                    navOptions = NavOptions
                        .Builder()
                        .setPopUpTo(HomeRoute.route, inclusive = true)
                        .build()
                )
            }
        )
    }
}