package com.sample.biometric

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.fragment.app.FragmentActivity
import com.sample.biometric.di.ViewModelFactory
import com.sample.biometric.ui.CompositionLocalProvider
import com.sample.biometric.ui.MainContainer
import dagger.android.AndroidInjection
import javax.inject.Inject

class MainActivity : FragmentActivity() {

    @Inject
    lateinit var compositionLocalProvider: CompositionLocalProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContent {
            compositionLocalProvider(viewModelStoreOwner = this) {
                MainContainer()
            }
        }
    }

}