package com.sample.biometric.ui.screen.biometric

import android.app.Activity.RESULT_CANCELED
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.R
import android.provider.Settings.ACTION_BIOMETRIC_ENROLL
import android.provider.Settings.ACTION_SECURITY_SETTINGS
import android.provider.Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import com.sample.biometric.common.findActivity

@Composable
fun BiometricSettingsContainer(
    onAuthSucceeded: () -> Unit = {},
    onAuthError: () -> Unit = {},
) {
    val launcher = rememberLauncherForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_CANCELED) {
                onAuthError()
                return@rememberLauncherForActivityResult
            }
            onAuthSucceeded()
        }

    val intent = if (SDK_INT < R) {
        Intent(ACTION_SECURITY_SETTINGS)
    } else {
        Intent(ACTION_BIOMETRIC_ENROLL).apply {
            putExtra(
                EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                BIOMETRIC_STRONG
            )
        }
    }

    LaunchedEffect(key1 = Unit) {
        launcher.launch(intent)
    }

}
