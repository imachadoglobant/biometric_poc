package com.sample.biometric.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.compose.rememberNavController
import com.sample.biometric.di.ViewModelFactory
import javax.inject.Inject

class CompositionLocalProvider @Inject constructor(
    private val viewModelFactory: ViewModelFactory
) {
    @Composable
    operator fun invoke(
        viewModelStoreOwner: ViewModelStoreOwner? = LocalViewModelStoreOwner.current,
        content: @Composable () -> Unit
    ) {
        CompositionLocalProvider(
            LocalViewModelFactory provides viewModelFactory,
            LocalViewModelStoreOwner provides viewModelStoreOwner,
            LocalNavController provides rememberNavController()
        ) {
            content()
        }
    }
}
