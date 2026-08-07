package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.auth.UserAccount
import com.example.ui.theme.EmeraldSuccess
import com.example.utils.Responsive

enum class StudentPortal(val label: String) {
    LIBRARY("Library"),
    EXAMS("Exams"),
    FINANCE("Finance"),
    SETTINGS("Settings")
}

data class MetricCardData(
    val title: String,
    val value: String,
    val subtitle: String,
    val icon: ImageVector,
    val accentColor: Color
)

@Composable
fun DashboardScreen(
    student: UserAccount,
    onNavigateTab: (Int) -> Unit,
    onOpenPortal: (StudentPortal) -> Unit,
    onLockApp: () -> Unit
) {
    val dimensions = Responsive.dimensions

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimensions.outerPadding)
            .testTag("dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(dimensions.moderateScale(16f))
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            DashboardHeader(student = student, onLockApp = onLockApp)
        }

        item {
            // Hero Welcome Banner
            HeroAcademicBanner()
        }

        item {
            // Quick-access portals for modules that previously had no entry point
            Text(
                text = "Academic Portals",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    PortalShortcutCard(StudentPortal.LIBRARY, Icons.Default.Book, onOpenPortal)
                    PortalShortcutCard(StudentPortal.EXAMS, Icons.AutoMirrored.Filled.EventNote, onOpenPortal)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    PortalShortcutCard(StudentPortal.FINANCE, Icons.Default.Payments, onOpenPortal)
                    PortalShortcutCard(StudentPortal.SETTINGS, Icons.Default.Fingerprint, onOpenPortal)
                }
            }
        }

        item {
            Text(
                text = "Academic Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // Responsive Adaptive Grid for Bento Cards
            val columns = dimensions.gridColumns
            val metrics = listOf(
                MetricCardData("Current GPA", "3.88 / 4.0", "+0.12 this term", Icons.Default.School, MaterialTheme.colorScheme.primary),
                MetricCardData("Attendance", "94.2%", "Required: 85.0%", Icons.Default.CheckCircle, EmeraldSuccess),
                MetricCardData("Upcoming Exams", "3 Scheduled", "Next: CS301 on Mon", Icons.AutoMirrored.Filled.EventNote, MaterialTheme.colorScheme.tertiary),
                MetricCardData("Fee Balance", "$0.00", "Clear for Fall 2026", Icons.Default.Payments, MaterialTheme.colorScheme.secondary)
            )

            if (columns == 1) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    metrics.forEach { metric ->
                        BentoMetricCard(metric = metric)
                    }
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val half = metrics.size / 2
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        for (i in 0 until half) BentoMetricCard(metric = metrics[i])
                    }
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        for (i in half until metrics.size) BentoMetricCard(metric = metrics[i])
                    }
                }
            }
        }

        item {
            Text(
                text = "Enrolled Courses",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                EnrolledCourseCard(
                    code = "CS301",
                    title = "Algorithms & Data Structures",
                    instructor = "Dr. Robert Vance",
                    progress = 0.82f,
                    nextClass = "Today, 02:00 PM (Lab 4B)"
                )
                EnrolledCourseCard(
                    code = "MATH204",
                    title = "Linear Algebra & Optimization",
                    instructor = "Prof. Elena Rostova",
                    progress = 0.68f,
                    nextClass = "Tomorrow, 10:00 AM (Hall C)"
                )
                EnrolledCourseCard(
                    code = "SE402",
                    title = "Mobile Application Architecture",
                    instructor = "Dr. Michael Zhang",
                    progress = 0.95f,
                    nextClass = "Wed, 11:30 AM (Studio 1)"
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DashboardHeader(student: UserAccount, onLockApp: () -> Unit) {
    val dimensions = Responsive.dimensions

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = student.fullName.take(1).uppercase(),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Welcome, ${student.fullName.split(" ").firstOrNull() ?: ""}",
                    fontSize = dimensions.responsiveSp(18f),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${student.department} • ${student.semester}",
                    fontSize = dimensions.responsiveSp(12f),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Row {
            IconButton(onClick = { /* Notifications */ }) {
                BadgedBox(badge = { Badge { Text("2") } }) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications"
                    )
                }
            }

            IconButton(
                onClick = onLockApp,
                modifier = Modifier.testTag("lock_app_icon_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Lock App",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun RowScope.PortalShortcutCard(
    portal: StudentPortal,
    icon: ImageVector,
    onOpenPortal: (StudentPortal) -> Unit
) {
    val dimensions = Responsive.dimensions

    Card(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(dimensions.cardCornerRadius))
            .clickable { onOpenPortal(portal) }
            .testTag("portal_shortcut_${portal.name.lowercase()}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = portal.label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun HeroAcademicBanner() {
    val dimensions = Responsive.dimensions

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimensions.cardCornerRadius)),
        colors = CardDefaults.cardColors(containerColor = Color.Unspecified)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "🔒 Biometric Session Active",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = "Fall Semester 2026",
                    fontSize = dimensions.responsiveSp(22f),
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Text(
                    text = "Midterm exams start in 5 days. Check schedule and download your digital admit card.",
                    fontSize = dimensions.responsiveSp(13f),
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun BentoMetricCard(metric: MetricCardData) {
    val dimensions = Responsive.dimensions

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimensions.cardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = metric.title,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = metric.value,
                    fontSize = dimensions.responsiveSp(20f),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
                Text(
                    text = metric.subtitle,
                    fontSize = 11.sp,
                    color = metric.accentColor,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(metric.accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = metric.icon,
                    contentDescription = metric.title,
                    tint = metric.accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun EnrolledCourseCard(
    code: String,
    title: String,
    instructor: String,
    progress: Float,
    nextClass: String
) {
    val dimensions = Responsive.dimensions

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimensions.cardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = code,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = "${(progress * 100).toInt()}% Complete",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = title,
                fontSize = dimensions.responsiveSp(15f),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = instructor,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = nextClass,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
