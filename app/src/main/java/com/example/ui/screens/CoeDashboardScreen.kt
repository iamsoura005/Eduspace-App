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
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.auth.AttendanceRecord
import com.example.auth.AttendanceStatus
import com.example.auth.UserAccount
import com.example.data.AppRepository
import com.example.ui.theme.EmeraldSuccess
import com.example.utils.Responsive

@Composable
fun CoeDashboardScreen(
    coeUser: UserAccount
) {
    val repository = AppRepository.instance
    val dimensions = Responsive.dimensions
    val records by repository.attendanceRecords.collectAsState()
    val users by repository.users.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterStatus by remember { mutableStateOf("ALL") }

    // Edit Modal State
    var editingRecord by remember { mutableStateOf<AttendanceRecord?>(null) }
    var selectedNewStatus by remember { mutableStateOf(AttendanceStatus.PRESENT) }

    val filteredRecords = records.filter { rec ->
        (selectedFilterStatus == "ALL" || rec.status.name.equals(selectedFilterStatus, ignoreCase = true)) &&
                (searchQuery.isEmpty() ||
                        rec.studentName.contains(searchQuery, ignoreCase = true) ||
                        rec.rollNumber.contains(searchQuery, ignoreCase = true) ||
                        rec.subjectCode.contains(searchQuery, ignoreCase = true) ||
                        rec.department.contains(searchQuery, ignoreCase = true))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = dimensions.outerPadding)
            .testTag("coe_dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // COE Header Card
        Card(
            modifier = Modifier.fillMaxWidth().testTag("coe_header_card"),
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
                        .background(MaterialTheme.colorScheme.tertiary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Badge, contentDescription = null, tint = MaterialTheme.colorScheme.onTertiary, modifier = Modifier.size(28.dp))
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = coeUser.fullName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "COE EXAM CONTROL",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "Emp ID: ${coeUser.roleSpecificId} • ${coeUser.department}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Overview Stats Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Total Records", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${records.size}", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                }
            }
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Present Today", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${records.count { it.status == AttendanceStatus.PRESENT }}", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = EmeraldSuccess)
                }
            }
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Absent / Late", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${records.count { it.status == AttendanceStatus.ABSENT || it.status == AttendanceStatus.LATE }}", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.error)
                }
            }
        }

        // Search & Status Filters
        Text("Attendance Records Management", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by Student, Roll No, Code...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().testTag("coe_search_input"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val filters = listOf("ALL", "PRESENT", "ABSENT", "LATE", "EXCUSED")
            items(filters) { status ->
                FilterChip(
                    selected = selectedFilterStatus == status,
                    onClick = { selectedFilterStatus = status },
                    label = { Text(status, fontSize = 12.sp) },
                    modifier = Modifier.testTag("coe_filter_$status")
                )
            }
        }

        // Attendance Table List with Full COE Controls
        filteredRecords.forEach { record ->
            Card(
                modifier = Modifier.fillMaxWidth().testTag("coe_record_${record.id}"),
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
                                Text(record.studentName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                                    Text(record.rollNumber, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                            Text("${record.subjectCode} - ${record.subjectName}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Date: ${record.date} • Last edit by: ${record.lastModifiedBy}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(record.status.badgeColorHex).copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = record.status.label.uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(record.status.badgeColorHex),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // COE Control Action Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (record.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                                contentDescription = null,
                                tint = if (record.isLocked) MaterialTheme.colorScheme.primary else EmeraldSuccess,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (record.isLocked) "Locked" else "Unlocked for Edit",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Edit Status Button
                            IconButton(
                                onClick = {
                                    editingRecord = record
                                    selectedNewStatus = record.status
                                },
                                modifier = Modifier.size(36.dp).testTag("coe_edit_${record.id}")
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Status", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            }

                            // Toggle Lock Status Button
                            IconButton(
                                onClick = { repository.coeToggleLockStatus(record.id, coeUser) },
                                modifier = Modifier.size(36.dp).testTag("coe_lock_toggle_${record.id}")
                            ) {
                                Icon(
                                    imageVector = if (record.isLocked) Icons.Default.LockOpen else Icons.Default.Lock,
                                    contentDescription = "Toggle Lock",
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Delete Record Button
                            IconButton(
                                onClick = { repository.coeDeleteAttendance(record.id, coeUser) },
                                modifier = Modifier.size(36.dp).testTag("coe_delete_${record.id}")
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Record", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // COE Edit Attendance Dialog
    if (editingRecord != null) {
        val rec = editingRecord!!
        AlertDialog(
            onDismissRequest = { editingRecord = null },
            title = { Text("Correct Attendance Status") },
            text = {
                Column {
                    Text("Student: ${rec.studentName} (${rec.rollNumber})", fontWeight = FontWeight.Bold)
                    Text("Subject: ${rec.subjectCode} - ${rec.subjectName}", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Select Corrected Status:", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                    AttendanceStatus.values().forEach { statusOption ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedNewStatus = statusOption }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = selectedNewStatus == statusOption,
                                onClick = { selectedNewStatus = statusOption }
                            )
                            Text(statusOption.label, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.coeUpdateAttendance(rec.id, selectedNewStatus, coeUser)
                        editingRecord = null
                    }
                ) {
                    Text("Save Correction")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingRecord = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
