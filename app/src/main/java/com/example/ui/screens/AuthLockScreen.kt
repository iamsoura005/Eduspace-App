package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.auth.AuthResult
import com.example.auth.BiometricAuthManager
import com.example.auth.BiometricStatus
import com.example.utils.Responsive

@Composable
fun AuthLockScreen(
    biometricAuthManager: BiometricAuthManager,
    isBiometricEnabled: Boolean,
    correctPin: String? = null,
    onSetPin: ((String) -> Unit)? = null,
    onUnlocked: () -> Unit
) {
    val dimensions = Responsive.dimensions
    val context = LocalContext.current
    val pinConfigured = !correctPin.isNullOrBlank()
    val setupPinRequired = !pinConfigured && onSetPin != null

    var isPinMode by remember { mutableStateOf(setupPinRequired || !isBiometricEnabled) }
    var enteredPin by remember { mutableStateOf("") }
    var isConfirmingNewPin by remember { mutableStateOf(false) }
    var pendingNewPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var statusInfo by remember { mutableStateOf("") }

    val biometricStatus = remember { biometricAuthManager.checkBiometricAvailability() }

    // Pulsing pulse animation for fingerprint icon
    val infiniteTransition = rememberInfiniteTransition(label = "Pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    fun handlePinInput(digit: String) {
        if (enteredPin.length < 4) {
            val updated = enteredPin + digit
            enteredPin = updated
            errorMessage = null
            if (updated.length == 4) {
                if (isConfirmingNewPin) {
                    if (updated == pendingNewPin) {
                        onSetPin?.invoke(updated)
                        onUnlocked()
                    } else {
                        errorMessage = "PINs did not match. Try again."
                        enteredPin = ""
                        isConfirmingNewPin = false
                        pendingNewPin = ""
                    }
                } else if (pinConfigured) {
                    if (updated == correctPin) {
                        onUnlocked()
                    } else {
                        errorMessage = "Incorrect Passcode. Try again."
                        enteredPin = ""
                    }
                } else {
                    // First entry of a brand-new PIN: ask for confirmation
                    pendingNewPin = updated
                    enteredPin = ""
                    isConfirmingNewPin = true
                }
            }
        }
    }

    fun isSettingUpPin() = setupPinRequired

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(dimensions.outerPadding),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(if (dimensions.isExpanded) 0.5f else if (dimensions.isMedium) 0.75f else 1f)
                .padding(16.dp)
                .testTag("auth_lock_card"),
            shape = RoundedCornerShape(dimensions.cardCornerRadius),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp, horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Header Lock Badge
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "E",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Eduspace Security",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = dimensions.responsiveSp(22f)
                )

                Text(
                    text = if (isSettingUpPin()) "Create Your 4-Digit Passcode"
                    else if (isPinMode) (if (isConfirmingNewPin) "Confirm Your 4-Digit Passcode" else "Enter 4-Digit Passcode")
                    else "Biometric Authentication Required",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                )

                if (!isPinMode) {
                    // Biometric Concentric Glowing Rings (Immersive UI Style)
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Outer Pulsing Glow Circle
                        Box(
                            modifier = Modifier
                                .size(150.dp)
                                .scale(pulseScale)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f), CircleShape)
                        )

                        // Inner Concentric Circle
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f), CircleShape)
                        )

                        // Center Biometric Trigger Button
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .clickable {
                                    val lockActivity = context as? androidx.fragment.app.FragmentActivity
                                    biometricAuthManager.promptBiometricAuth(
                                        activity = lockActivity ?: return@clickable,
                                        title = "Unlock Eduspace Session",
                                        subtitle = "Confirm identity with fingerprint or face",
                                        onResult = { result ->
                                            if (result is AuthResult.Success) {
                                                onUnlocked()
                                            } else if (result is AuthResult.Error) {
                                                errorMessage = result.errString
                                            }
                                        }
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Fingerprint Sensor",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(44.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = when (biometricStatus) {
                            is BiometricStatus.Ready -> "Use biometrics to unlock your secure session"
                            is BiometricStatus.NotEnrolled -> (biometricStatus as BiometricStatus.NotEnrolled).message
                            is BiometricStatus.HardwareMissing -> (biometricStatus as BiometricStatus.HardwareMissing).message
                            is BiometricStatus.Unavailable -> (biometricStatus as BiometricStatus.Unavailable).reason
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (isSettingUpPin()) {
                                isPinMode = true
                            } else {
                                val lockActivity = context as? androidx.fragment.app.FragmentActivity
                                biometricAuthManager.promptBiometricAuth(
                                    activity = lockActivity ?: return@Button,
                                    title = "Unlock Eduspace Session",
                                    subtitle = "Confirm identity with fingerprint or face",
                                    onResult = { result ->
                                        if (result is AuthResult.Success) {
                                            onUnlocked()
                                        } else if (result is AuthResult.Error) {
                                            errorMessage = result.errString
                                        }
                                    }
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("biometric_authenticate_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(26.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = if (isSettingUpPin()) "SET UP PASSCODE" else "AUTHENTICATE SESSION",
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { isPinMode = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("switch_to_pin_btn"),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp).padding(end = 6.dp)
                        )
                        Text("USE DEVICE PASSCODE")
                    }
                } else {
                    // PIN Entry Mode
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        for (i in 0 until 4) {
                            val isFilled = i < enteredPin.length
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isFilled) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.outline,
                                        shape = CircleShape
                                    )
                            )
                        }
                    }

                    errorMessage?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 3x4 Keypad
                    val keys = listOf(
                        listOf("1", "2", "3"),
                        listOf("4", "5", "6"),
                        listOf("7", "8", "9"),
                        listOf("Clear", "0", "Del")
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        for (row in keys) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                for (key in row) {
                                    Surface(
                                        modifier = Modifier
                                            .size(dimensions.moderateScale(56f))
                                            .clip(CircleShape)
                                            .clickable {
                                                when (key) {
                                                    "Clear" -> {
                                                        enteredPin = ""
                                                        errorMessage = null
                                                    }
                                                    "Del" -> {
                                                        if (enteredPin.isNotEmpty()) {
                                                            enteredPin = enteredPin.dropLast(1)
                                                        }
                                                    }
                                                    else -> handlePinInput(key)
                                                }
                                            }
                                            .testTag("keypad_$key"),
                                        shape = CircleShape,
                                        color = if (key == "Clear" || key == "Del")
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            if (key == "Del") {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                                                    contentDescription = "Delete",
                                                    modifier = Modifier.size(20.dp),
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            } else {
                                                Text(
                                                    text = key,
                                                    fontSize = dimensions.responsiveSp(18f),
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isBiometricEnabled) {
                        OutlinedButton(
                            onClick = { isPinMode = false },
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp).padding(end = 6.dp)
                            )
                            Text("Switch to Biometrics")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Immersive UI Footer Security Indicator
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SYSTEM SECURED // ENCRYPTION ACTIVE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (!pinConfigured) {
                    // First-time hint to set a PIN (removed once a PIN exists)
                    Text(
                        text = if (isSettingUpPin()) "First time here? Set your own passcode to secure this device." else "Demo PIN: 1234",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.clickable {
                            if (isSettingUpPin()) {
                                isPinMode = true
                            } else {
                                onUnlocked()
                            }
                        }
                    )
                }
            }
        }
    }
}
