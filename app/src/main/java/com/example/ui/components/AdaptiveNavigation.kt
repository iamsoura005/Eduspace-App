package com.example.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import com.example.auth.UserRole
import com.example.utils.Responsive

data class NavDestination(
    val route: String,
    val title: String,
    val icon: ImageVector
)

val StudentNavDestinations = listOf(
    NavDestination("dashboard", "Dashboard", Icons.Default.Dashboard),
    NavDestination("attendance", "Mark Class", Icons.Default.HowToReg),
    NavDestination("courses", "Courses", Icons.Default.School),
    NavDestination("timetable", "Schedule", Icons.Default.CalendarMonth),
    NavDestination("profile", "Profile", Icons.Default.Person)
)

val CoeNavDestinations = listOf(
    NavDestination("coe_dashboard", "COE Control", Icons.Default.Badge),
    NavDestination("courses", "Roster", Icons.Default.School),
    NavDestination("timetable", "Exam Schedule", Icons.Default.CalendarMonth),
    NavDestination("profile", "Profile", Icons.Default.Person)
)

val AdminNavDestinations = listOf(
    NavDestination("admin_dashboard", "Admin Portal", Icons.Default.AdminPanelSettings),
    NavDestination("logs", "Audit Logs", Icons.Default.ListAlt),
    NavDestination("profile", "Profile", Icons.Default.Person)
)

@Composable
fun AdaptiveNavigationContainer(
    role: UserRole = UserRole.STUDENT,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    content: @Composable () -> Unit
) {
    val dimensions = Responsive.dimensions
    val useRail = dimensions.isExpanded || (dimensions.isMedium && dimensions.isLandscape)

    val destinations = when (role) {
        UserRole.STUDENT -> StudentNavDestinations
        UserRole.COE -> CoeNavDestinations
        UserRole.IT_ADMIN -> AdminNavDestinations
    }

    if (useRail) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            NavigationRail(
                modifier = Modifier
                    .fillMaxHeight()
                    .testTag("adaptive_nav_rail"),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                destinations.forEachIndexed { index, nav ->
                    NavigationRailItem(
                        selected = selectedTab == index,
                        onClick = { onTabSelected(index) },
                        icon = { Icon(imageVector = nav.icon, contentDescription = nav.title) },
                        label = { Text(nav.title) },
                        modifier = Modifier.testTag("nav_rail_item_$index")
                    )
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                content()
            }
        }
    } else {
        Scaffold(
            bottomBar = {
                NavigationBar(
                    modifier = Modifier.testTag("adaptive_nav_bar"),
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    destinations.forEachIndexed { index, nav ->
                        NavigationBarItem(
                            selected = selectedTab == index,
                            onClick = { onTabSelected(index) },
                            icon = { Icon(imageVector = nav.icon, contentDescription = nav.title) },
                            label = { Text(nav.title) },
                            modifier = Modifier.testTag("nav_bar_item_$index")
                        )
                    }
                }
            },
            contentWindowInsets = WindowInsets.statusBars
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                content()
            }
        }
    }
}
