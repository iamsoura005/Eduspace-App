package com.example

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.example.auth.BiometricAuthManager
import com.example.auth.UserRole
import com.example.data.AppRepository
import com.example.data.SecurityPreferences
import com.example.ui.components.AdaptiveNavigationContainer
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AuthLockScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CoeDashboardScreen
import com.example.ui.screens.CoursesScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ExamsScreen
import com.example.ui.screens.FinanceScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.StudentDashboardScreen
import com.example.ui.screens.StudentPortal
import com.example.ui.screens.TimetableScreen
import com.example.ui.screens.UserProfileScreen
import com.example.ui.theme.NoteLoomTheme
import com.example.utils.ProvideResponsiveDimensions
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {

    private lateinit var securityPreferences: SecurityPreferences
    private lateinit var biometricAuthManager: BiometricAuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        securityPreferences = SecurityPreferences(applicationContext)
        biometricAuthManager = BiometricAuthManager(applicationContext)

        setContent {
            ProvideResponsiveDimensions {
                NoteLoomTheme {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        MainAppContent(
                            activity = this@MainActivity,
                            securityPreferences = securityPreferences,
                            biometricAuthManager = biometricAuthManager
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainAppContent(
    activity: FragmentActivity,
    securityPreferences: SecurityPreferences,
    biometricAuthManager: BiometricAuthManager
) {
    val repository = AppRepository.instance
    val currentUser by repository.currentUser.collectAsState()
    val scope = rememberCoroutineScope()

    val isBiometricEnabled by securityPreferences.isBiometricEnabled.collectAsState(initial = false)
    val sessionTimeoutMinutes by securityPreferences.sessionTimeoutMinutes.collectAsState(initial = 1)
    val requireBiometricForPayments by securityPreferences.requireBiometricForPayments.collectAsState(initial = true)
    val correctPin by securityPreferences.userPin.collectAsState(initial = "")

    var showSplash by remember { mutableStateOf(true) }
    var isLocked by remember { mutableStateOf(false) }
    var activeTab by remember { mutableIntStateOf(0) }
    var openPortal by remember { mutableStateOf<StudentPortal?>(null) }

    LaunchedEffect(currentUser) {
        // Reset tab index and portal on role change or re-login
        activeTab = 0
        openPortal = null
    }

    if (showSplash) {
        SplashScreen(
            onSplashFinished = {
                showSplash = false
            }
        )
    } else if (currentUser == null) {
        // AUTHENTICATION SCREEN (Role-Based Login / Signup + Biometric Quick Sign-In)
        AuthScreen(
            securityPreferences = securityPreferences,
            biometricAuthManager = biometricAuthManager,
            onLoginSuccess = { user ->
                repository.setCurrentUser(user)
                activeTab = 0
            }
        )
    } else if (isLocked) {
        // APP LOCK SCREEN
        AuthLockScreen(
            biometricAuthManager = biometricAuthManager,
            isBiometricEnabled = isBiometricEnabled,
            correctPin = correctPin,
            onSetPin = { pin ->
                // Persist the new PIN for future lock screens
                scope.launch {
                    securityPreferences.setUserPin(pin)
                }
            },
            onUnlocked = {
                isLocked = false
            }
        )
    } else {
        // ROLE-BASED ADAPTIVE ERP NAVIGATION
        val user = currentUser!!

        if (user.role == UserRole.STUDENT && openPortal != null) {
            // Full-screen portals (Library / Exams / Finance / Settings)
            val portal = openPortal!!
            PortalScaffold(title = portal.label, onBack = { openPortal = null }) {
                when (portal) {
                    StudentPortal.LIBRARY -> LibraryScreen()
                    StudentPortal.EXAMS -> ExamsScreen()
                    StudentPortal.FINANCE -> FinanceScreen(
                        biometricAuthManager = biometricAuthManager,
                        requireBiometricForPayments = requireBiometricForPayments
                    )
                    StudentPortal.SETTINGS -> SettingsScreen(
                        user = user,
                        securityPreferences = securityPreferences,
                        isBiometricEnabled = isBiometricEnabled,
                        sessionTimeoutMinutes = sessionTimeoutMinutes,
                        requireBiometricForPayments = requireBiometricForPayments,
                        onLockApp = { isLocked = true }
                    )
                }
            }
        } else {
            AdaptiveNavigationContainer(
                role = user.role,
                selectedTab = activeTab,
                onTabSelected = { activeTab = it }
            ) {
                when (user.role) {
                    UserRole.STUDENT -> {
                        when (activeTab) {
                            0 -> DashboardScreen(
                                student = user,
                                onNavigateTab = { activeTab = it },
                                onOpenPortal = { openPortal = it },
                                onLockApp = { isLocked = true }
                            )
                            1 -> StudentDashboardScreen(
                                student = user,
                                onNavigateToSchedule = { activeTab = 3 }
                            )
                            2 -> CoursesScreen()
                            3 -> TimetableScreen()
                            4 -> UserProfileScreen(
                                user = user,
                                onLogout = {
                                scope.launch {
                                    securityPreferences.clearLastLoggedInEmail()
                                }
                                repository.logout()
                            },
                                onLockApp = { isLocked = true }
                            )
                            else -> DashboardScreen(
                                student = user,
                                onNavigateTab = { activeTab = it },
                                onOpenPortal = { openPortal = it },
                                onLockApp = { isLocked = true }
                            )
                        }
                    }
                    UserRole.COE -> {
                        when (activeTab) {
                            0 -> CoeDashboardScreen(coeUser = user)
                            1 -> CoursesScreen()
                            2 -> TimetableScreen()
                            3 -> UserProfileScreen(
                                user = user,
                                onLogout = {
                                scope.launch {
                                    securityPreferences.clearLastLoggedInEmail()
                                }
                                repository.logout()
                            },
                                onLockApp = { isLocked = true }
                            )
                            else -> CoeDashboardScreen(coeUser = user)
                        }
                    }
                    UserRole.IT_ADMIN -> {
                        when (activeTab) {
                            0 -> AdminDashboardScreen(adminUser = user, initialSection = 0)
                            1 -> AdminDashboardScreen(adminUser = user, initialSection = 1)
                            2 -> UserProfileScreen(
                                user = user,
                                onLogout = {
                                scope.launch {
                                    securityPreferences.clearLastLoggedInEmail()
                                }
                                repository.logout()
                            },
                                onLockApp = { isLocked = true }
                            )
                            else -> AdminDashboardScreen(adminUser = user, initialSection = 0)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PortalScaffold(
    title: String,
    onBack: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Box(modifier = Modifier.weight(1f)) {
            content()
        }
    }
}
