package com.sample.biometric.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

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

fun <T> MutableStateFlow<ViewState<T>>.modify(callback: (value: T) -> T) {
    val state = (value as? ViewState.Success)?.data ?: return

    update {
        ViewState.Success(
            callback(state)
        )
    }
}