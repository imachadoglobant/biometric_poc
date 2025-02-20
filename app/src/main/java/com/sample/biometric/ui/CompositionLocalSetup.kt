package com.sample.biometric.ui

import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavHostController
import com.sample.biometric.di.ViewModelFactory

val LocalViewModelFactory =
    compositionLocalOf<ViewModelFactory> { error("No view model factory found") }
val LocalViewModelStoreOwner =
    compositionLocalOf<ViewModelStoreOwner?> { error("No storeOwner found") }
val LocalNavController = compositionLocalOf<NavHostController> {
    error("No NavController found!")
}
