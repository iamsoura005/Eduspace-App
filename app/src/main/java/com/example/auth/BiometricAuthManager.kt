package com.example.auth

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

sealed class BiometricStatus {
    object Ready : BiometricStatus()
    data class Unavailable(val reason: String) : BiometricStatus()
    data class NotEnrolled(val message: String) : BiometricStatus()
    data class HardwareMissing(val message: String) : BiometricStatus()
}

sealed class AuthResult {
    object Success : AuthResult()
    data class Error(val code: Int, val errString: String) : AuthResult()
    object Failed : AuthResult()
    object UsePasscode : AuthResult()
}

class BiometricAuthManager(private val context: Context) {

    private val biometricManager = BiometricManager.from(context)

    fun checkBiometricAvailability(): BiometricStatus {
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.BIOMETRIC_WEAK

        return when (biometricManager.canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricStatus.Ready
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricStatus.HardwareMissing(
                "No biometric hardware detected on this device."
            )
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricStatus.Unavailable(
                "Biometric sensor is currently unavailable. Please try again."
            )
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricStatus.NotEnrolled(
                "No fingerprint or face credential enrolled. Please set up biometrics in device settings."
            )
            else -> BiometricStatus.Unavailable("Biometric authentication is unavailable.")
        }
    }

    fun promptBiometricAuth(
        activity: FragmentActivity,
        title: String = "Biometric Verification",
        subtitle: String = "Confirm identity using fingerprint or face unlock",
        negativeButtonText: String = "Use PIN / Passcode",
        onResult: (AuthResult) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(context)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onResult(AuthResult.Success)
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON ||
                    errorCode == BiometricPrompt.ERROR_USER_CANCELED) {
                    onResult(AuthResult.UsePasscode)
                } else {
                    onResult(AuthResult.Error(errorCode, errString.toString()))
                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onResult(AuthResult.Failed)
            }
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setNegativeButtonText(negativeButtonText)
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK
            )
            .build()

        try {
            val biometricPrompt = BiometricPrompt(activity, executor, callback)
            biometricPrompt.authenticate(promptInfo)
        } catch (e: Exception) {
            onResult(AuthResult.Error(-1, e.localizedMessage ?: "Failed to initialize prompt"))
        }
    }
}
