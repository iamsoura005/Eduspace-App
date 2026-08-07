package com.example

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.StudentDashboardScreen
import com.example.ui.screens.TimetableScreen
import com.example.ui.screens.UserProfileScreen
import com.example.ui.theme.NoteLoomTheme
import com.example.utils.ProvideResponsiveDimensions

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

    val isBiometricEnabled by securityPreferences.isBiometricEnabled.collectAsState(initial = false)
    val correctPin by securityPreferences.userPin.collectAsState(initial = "1234")

    var showSplash by remember { mutableStateOf(true) }
    var isLocked by remember { mutableStateOf(false) }
    var activeTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(currentUser) {
        // Reset tab index on role change or re-login
        activeTab = 0
    }

    if (showSplash) {
        SplashScreen(
            onSplashFinished = {
                showSplash = false
            }
        )
    } else if (currentUser == null) {
        // AUTHENTICATION SCREEN (Role-Based Login / Signup)
        AuthScreen(
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
            onUnlocked = {
                isLocked = false
            }
        )
    } else {
        // ROLE-BASED ADAPTIVE ERP NAVIGATION
        val user = currentUser!!

        AdaptiveNavigationContainer(
            role = user.role,
            selectedTab = activeTab,
            onTabSelected = { activeTab = it }
        ) {
            when (user.role) {
                UserRole.STUDENT -> {
                    when (activeTab) {
                        0 -> StudentDashboardScreen(student = user)
                        1 -> StudentDashboardScreen(student = user)
                        2 -> CoursesScreen()
                        3 -> TimetableScreen()
                        4 -> UserProfileScreen(
                            user = user,
                            onLogout = { repository.logout() },
                            onLockApp = { isLocked = true }
                        )
                        else -> StudentDashboardScreen(student = user)
                    }
                }
                UserRole.COE -> {
                    when (activeTab) {
                        0 -> CoeDashboardScreen(coeUser = user)
                        1 -> CoursesScreen()
                        2 -> TimetableScreen()
                        3 -> UserProfileScreen(
                            user = user,
                            onLogout = { repository.logout() },
                            onLockApp = { isLocked = true }
                        )
                        else -> CoeDashboardScreen(coeUser = user)
                    }
                }
                UserRole.IT_ADMIN -> {
                    when (activeTab) {
                        0 -> AdminDashboardScreen(adminUser = user)
                        1 -> AdminDashboardScreen(adminUser = user)
                        2 -> UserProfileScreen(
                            user = user,
                            onLogout = { repository.logout() },
                            onLockApp = { isLocked = true }
                        )
                        else -> AdminDashboardScreen(adminUser = user)
                    }
                }
            }
        }
    }
}
