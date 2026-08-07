package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.auth.UserAccount
import com.example.auth.UserRole
import com.example.auth.UserStatus
import com.example.data.AppRepository
import com.example.ui.theme.EmeraldSuccess
import com.example.utils.Responsive

@Composable
fun AdminDashboardScreen(
    adminUser: UserAccount,
    initialSection: Int = 0
) {
    val repository = AppRepository.instance
    val dimensions = Responsive.dimensions

    val users by repository.users.collectAsState()
    val logs by repository.activityLogs.collectAsState()
    val attendanceRecords by repository.attendanceRecords.collectAsState()

    var activeTab by remember { mutableIntStateOf(initialSection.coerceIn(0, 2)) } // 0: Users, 1: Activity Logs, 2: Reports
    var searchQuery by remember { mutableStateOf("") }
    var roleFilter by remember { mutableStateOf("ALL") }

    // Dialog states
    var showAddUserDialog by remember { mutableStateOf(false) }
    var editingUser by remember { mutableStateOf<UserAccount?>(null) }
    var resetPassUser by remember { mutableStateOf<UserAccount?>(null) }
    var newPasswordInput by remember { mutableStateOf("") }
    var reportExportFeedback by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = dimensions.outerPadding)
            .testTag("admin_dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // IT Admin Header Banner
        Card(
            modifier = Modifier.fillMaxWidth().testTag("admin_header_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(28.dp))
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = adminUser.fullName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                            Text("IT ADMIN", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                    Text("Sec ID: ${adminUser.roleSpecificId} • ${adminUser.department}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        // Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Total Accounts", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${users.size}", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                }
            }
            Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Active Sessions", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${users.count { it.status == UserStatus.ACTIVE }}", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = EmeraldSuccess)
                }
            }
            Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("System Logs", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${logs.size}", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.tertiary)
                }
            }
        }

        // Section Tabs
        TabRow(
            selectedTabIndex = activeTab,
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).testTag("admin_tabs")
        ) {
            Tab(selected = activeTab == 0, onClick = { activeTab = 0 }, text = { Text("User Accounts", fontWeight = FontWeight.Bold) }, modifier = Modifier.testTag("tab_users"))
            Tab(selected = activeTab == 1, onClick = { activeTab = 1 }, text = { Text("Activity Logs", fontWeight = FontWeight.Bold) }, modifier = Modifier.testTag("tab_logs"))
            Tab(selected = activeTab == 2, onClick = { activeTab = 2 }, text = { Text("Reports & Export", fontWeight = FontWeight.Bold) }, modifier = Modifier.testTag("tab_reports"))
        }

        if (activeTab == 0) {
            // USER MANAGEMENT
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Manage System Users", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Button(
                    onClick = { showAddUserDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("add_user_btn")
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp).padding(end = 4.dp))
                    Text("ADD USER", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search users by Name, Email, or ID...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().testTag("admin_search_users"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val roles = listOf("ALL", "STUDENT", "COE", "IT_ADMIN")
                items(roles) { r ->
                    FilterChip(
                        selected = roleFilter == r,
                        onClick = { roleFilter = r },
                        label = { Text(r, fontSize = 12.sp) }
                    )
                }
            }

            val filteredUsers = users.filter { u ->
                (roleFilter == "ALL" || u.role.name == roleFilter) &&
                        (searchQuery.isEmpty() ||
                                u.fullName.contains(searchQuery, ignoreCase = true) ||
                                u.email.contains(searchQuery, ignoreCase = true) ||
                                u.roleSpecificId.contains(searchQuery, ignoreCase = true))
            }

            filteredUsers.forEach { user ->
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("user_item_${user.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(user.fullName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (user.status == UserStatus.ACTIVE) EmeraldSuccess.copy(alpha = 0.2f) else MaterialTheme.colorScheme.errorContainer
                                    ) {
                                        Text(
                                            text = user.status.label.uppercase(),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (user.status == UserStatus.ACTIVE) EmeraldSuccess else MaterialTheme.colorScheme.error,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text("${user.role.label} • ID: ${user.roleSpecificId}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${user.email} • ${user.department}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                // Reset Pass
                                IconButton(
                                    onClick = {
                                        resetPassUser = user
                                        newPasswordInput = ""
                                    },
                                    modifier = Modifier.size(36.dp).testTag("reset_pass_${user.id}")
                                ) {
                                    Icon(Icons.Default.LockReset, contentDescription = "Reset Password", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                }

                                // Suspend/Activate
                                IconButton(
                                    onClick = { repository.adminToggleUserStatus(user.id, adminUser) },
                                    modifier = Modifier.size(36.dp).testTag("toggle_status_${user.id}")
                                ) {
                                    Icon(
                                        imageVector = if (user.status == UserStatus.ACTIVE) Icons.Default.Block else Icons.Default.CheckCircle,
                                        contentDescription = "Toggle Status",
                                        tint = if (user.status == UserStatus.ACTIVE) MaterialTheme.colorScheme.error else EmeraldSuccess,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                // Delete
                                if (user.id != adminUser.id) {
                                    IconButton(
                                        onClick = { repository.adminDeleteUser(user.id, adminUser) },
                                        modifier = Modifier.size(36.dp).testTag("delete_user_${user.id}")
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete User", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (activeTab == 1) {
            // ACTIVITY LOGS
            Text("System Audit Trail & Security Logs", fontSize = 18.sp, fontWeight = FontWeight.Bold)

            logs.forEach { log ->
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("log_item_${log.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(log.action, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                            Text(log.timestamp, fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("User: ${log.userName} (${log.userRole.label} - ${log.userId})", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Text(log.details, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            // REPORTS & EXPORT
            Text("Attendance Analytics & Report Exports", fontSize = 18.sp, fontWeight = FontWeight.Bold)

            if (reportExportFeedback != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EmeraldSuccess.copy(alpha = 0.2f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = reportExportFeedback!!,
                        color = EmeraldSuccess,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Export System Attendance Summary", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Select format to generate comprehensive institutional attendance audit.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { reportExportFeedback = "Exported PDF Report: Eduspace_Attendance_Summary.pdf" },
                            modifier = Modifier.weight(1f).testTag("export_pdf_btn"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp).padding(end = 4.dp))
                            Text("PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { reportExportFeedback = "Exported Excel Sheet: Eduspace_Attendance_Data.xlsx" },
                            modifier = Modifier.weight(1f).testTag("export_excel_btn"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp).padding(end = 4.dp))
                            Text("Excel", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { reportExportFeedback = "Exported CSV File: Eduspace_Attendance_Records.csv" },
                            modifier = Modifier.weight(1f).testTag("export_csv_btn"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp).padding(end = 4.dp))
                            Text("CSV", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Add User Dialog
    if (showAddUserDialog) {
        var newRole by remember { mutableStateOf(UserRole.STUDENT) }
        var newName by remember { mutableStateOf("") }
        var newEmail by remember { mutableStateOf("") }
        var newPhone by remember { mutableStateOf("") }
        var newDept by remember { mutableStateOf("Computer Science") }
        var newRoleId by remember { mutableStateOf("") }
        var newPass by remember { mutableStateOf("123456") }

        AlertDialog(
            onDismissRequest = { showAddUserDialog = false },
            title = { Text("Provision New Account") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("Select User Role:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        UserRole.values().forEach { r ->
                            FilterChip(
                                selected = newRole == r,
                                onClick = { newRole = r },
                                label = { Text(r.name, fontSize = 10.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = newName, onValueChange = { newName = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = newEmail, onValueChange = { newEmail = it }, label = { Text("Official Email") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = newPhone, onValueChange = { newPhone = it }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = newDept, onValueChange = { newDept = it }, label = { Text("Department") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = newRoleId, onValueChange = { newRoleId = it }, label = { Text("Role Specific ID (STU/COE/ADM)") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = newPass, onValueChange = { newPass = it }, label = { Text("Initial Password") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isNotBlank() && newEmail.isNotBlank()) {
                            val created = UserAccount(
                                id = "U-${System.currentTimeMillis().toString().takeLast(6)}",
                                fullName = newName,
                                email = newEmail,
                                phone = newPhone.ifBlank { "+1 (555) 000-0000" },
                                role = newRole,
                                department = newDept,
                                roleSpecificId = newRoleId.ifBlank { "${newRole.name}-${System.currentTimeMillis().toString().takeLast(4)}" },
                                passwordHash = newPass
                            )
                            repository.adminAddUser(created, adminUser)
                            showAddUserDialog = false
                        }
                    }
                ) {
                    Text("Provision Account")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddUserDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Reset Password Dialog
    if (resetPassUser != null) {
        val target = resetPassUser!!
        AlertDialog(
            onDismissRequest = { resetPassUser = null },
            title = { Text("Reset Password") },
            text = {
                Column {
                    Text("Resetting password for ${target.fullName} (${target.email})")
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = newPasswordInput,
                        onValueChange = { newPasswordInput = it },
                        label = { Text("New Password") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPasswordInput.isNotBlank()) {
                            repository.adminResetPassword(target.id, newPasswordInput, adminUser)
                            resetPassUser = null
                        }
                    }
                ) {
                    Text("Reset Password")
                }
            },
            dismissButton = {
                TextButton(onClick = { resetPassUser = null }) { Text("Cancel") }
            }
        )
    }
}
