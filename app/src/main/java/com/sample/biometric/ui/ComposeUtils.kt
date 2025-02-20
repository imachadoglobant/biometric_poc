package com.sample.biometric.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
inline fun <reified VM : ViewModel> retrieveViewModel(): VM {
    return viewModel(
        viewModelStoreOwner = checkNotNull(
            LocalViewModelStoreOwner.current ?: LocalViewModelStoreOwner.current
        ) {
            "No ViewModelStoreOwner was provided via LocalRoyalViewModelStoreOwner"
        },
        factory = LocalViewModelFactory.current
    )
}
