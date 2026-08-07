package com.example.data

import com.example.auth.ActivityLogItem
import com.example.auth.AttendanceRecord
import com.example.auth.AttendanceStatus
import com.example.auth.UserAccount
import com.example.auth.UserRole
import com.example.auth.UserStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AppRepository private constructor() {

    companion object {
        val instance: AppRepository by lazy { AppRepository() }
    }

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    // Initial Demo Users
    private val _users = MutableStateFlow<List<UserAccount>>(
        listOf(
            UserAccount(
                id = "U-STU-01",
                fullName = "Alex Mercer",
                email = "alex.student@eduspace.edu",
                phone = "+1 (555) 234-5678",
                role = UserRole.STUDENT,
                department = "Computer Science",
                roleSpecificId = "STU-2026-8812",
                rollNumber = "CS-2026-042",
                semester = "Semester 6",
                status = UserStatus.ACTIVE,
                passwordHash = "student123"
            ),
            UserAccount(
                id = "U-STU-02",
                fullName = "Sophia Chen",
                email = "sophia.student@eduspace.edu",
                phone = "+1 (555) 345-6789",
                role = UserRole.STUDENT,
                department = "Computer Science",
                roleSpecificId = "STU-2026-8815",
                rollNumber = "CS-2026-045",
                semester = "Semester 6",
                status = UserStatus.ACTIVE,
                passwordHash = "student123"
            ),
            UserAccount(
                id = "U-STU-03",
                fullName = "David Kim",
                email = "david.student@eduspace.edu",
                phone = "+1 (555) 456-7890",
                role = UserRole.STUDENT,
                department = "Electrical Engineering",
                roleSpecificId = "STU-2026-9012",
                rollNumber = "EE-2026-011",
                semester = "Semester 4",
                status = UserStatus.ACTIVE,
                passwordHash = "student123"
            ),
            UserAccount(
                id = "U-COE-01",
                fullName = "Dr. Clara Oswald",
                email = "clara.coe@eduspace.edu",
                phone = "+1 (555) 876-5432",
                role = UserRole.COE,
                department = "Controller of Exams Office",
                roleSpecificId = "COE-EMP-401",
                status = UserStatus.ACTIVE,
                passwordHash = "coe123"
            ),
            UserAccount(
                id = "U-ADM-01",
                fullName = "Marcus Vance",
                email = "admin@eduspace.edu",
                phone = "+1 (555) 999-0000",
                role = UserRole.IT_ADMIN,
                department = "IT Infrastructure & Security",
                roleSpecificId = "ADM-SYS-001",
                status = UserStatus.ACTIVE,
                passwordHash = "admin123"
            )
        )
    )
    val users: StateFlow<List<UserAccount>> = _users.asStateFlow()

    // Current Authenticated User State
    private val _currentUser = MutableStateFlow<UserAccount?>(_users.value.first()) // Default to student
    val currentUser: StateFlow<UserAccount?> = _currentUser.asStateFlow()

    // Attendance Records Store
    private val _attendanceRecords = MutableStateFlow<List<AttendanceRecord>>(
        listOf(
            AttendanceRecord(
                id = "ATT-101",
                studentId = "STU-2026-8812",
                studentName = "Alex Mercer",
                rollNumber = "CS-2026-042",
                department = "Computer Science",
                subjectCode = "MATH204",
                subjectName = "Linear Algebra & Optimization",
                date = dateFormat.format(Date()),
                timeSlot = "09:00 AM - 10:30 AM",
                teacherName = "Prof. Elena Rostova",
                status = AttendanceStatus.PRESENT,
                isLocked = true,
                markedAtTime = "09:05 AM",
                lastModifiedBy = "Alex Mercer (Student)"
            ),
            AttendanceRecord(
                id = "ATT-102",
                studentId = "STU-2026-8812",
                studentName = "Alex Mercer",
                rollNumber = "CS-2026-042",
                department = "Computer Science",
                subjectCode = "CS301",
                subjectName = "Algorithms & Data Structures",
                date = dateFormat.format(Date()),
                timeSlot = "11:00 AM - 12:30 PM",
                teacherName = "Dr. Robert Vance",
                status = AttendanceStatus.PRESENT,
                isLocked = true,
                markedAtTime = "11:02 AM",
                lastModifiedBy = "Alex Mercer (Student)"
            ),
            AttendanceRecord(
                id = "ATT-103",
                studentId = "STU-2026-8815",
                studentName = "Sophia Chen",
                rollNumber = "CS-2026-045",
                department = "Computer Science",
                subjectCode = "CS301",
                subjectName = "Algorithms & Data Structures",
                date = dateFormat.format(Date()),
                timeSlot = "11:00 AM - 12:30 PM",
                teacherName = "Dr. Robert Vance",
                status = AttendanceStatus.LATE,
                isLocked = true,
                markedAtTime = "11:22 AM",
                lastModifiedBy = "Dr. Clara Oswald (COE)"
            ),
            AttendanceRecord(
                id = "ATT-104",
                studentId = "STU-2026-9012",
                studentName = "David Kim",
                rollNumber = "EE-2026-011",
                department = "Electrical Engineering",
                subjectCode = "EE201",
                subjectName = "Circuits & Signal Processing",
                date = dateFormat.format(Date()),
                timeSlot = "09:00 AM - 10:30 AM",
                teacherName = "Prof. Alan Turing",
                status = AttendanceStatus.ABSENT,
                isLocked = true,
                markedAtTime = "09:30 AM",
                lastModifiedBy = "System Auto-Absence"
            )
        )
    )
    val attendanceRecords: StateFlow<List<AttendanceRecord>> = _attendanceRecords.asStateFlow()

    // Activity Logs
    private val _activityLogs = MutableStateFlow<List<ActivityLogItem>>(
        listOf(
            ActivityLogItem(
                id = "LOG-001",
                timestamp = dateTimeFormat.format(Date()),
                userId = "ADM-SYS-001",
                userName = "Marcus Vance",
                userRole = UserRole.IT_ADMIN,
                action = "System Startup",
                details = "Eduspace ERP Security & RBAC Engine initialized."
            ),
            ActivityLogItem(
                id = "LOG-002",
                timestamp = dateTimeFormat.format(Date()),
                userId = "STU-2026-8812",
                userName = "Alex Mercer",
                userRole = UserRole.STUDENT,
                action = "Attendance Marked",
                details = "Marked PRESENT for MATH204 (Linear Algebra)."
            )
        )
    )
    val activityLogs: StateFlow<List<ActivityLogItem>> = _activityLogs.asStateFlow()

    fun setCurrentUser(user: UserAccount?) {
        _currentUser.value = user
        if (user != null) {
            logActivity(user, "User Login", "User ${user.fullName} (${user.role.label}) logged in successfully.")
        }
    }

    fun logout() {
        val user = _currentUser.value
        if (user != null) {
            logActivity(user, "User Logout", "User ${user.fullName} logged out.")
        }
        _currentUser.value = null
    }

    fun authenticate(email: String, password: String, role: UserRole): UserAccount? {
        val found = _users.value.find { 
            it.email.equals(email.trim(), ignoreCase = true) && 
            it.passwordHash == password &&
            it.role == role
        }
        if (found != null && found.status == UserStatus.ACTIVE) {
            setCurrentUser(found)
            return found
        }
        return null
    }

    fun registerUser(newUser: UserAccount): Boolean {
        if (_users.value.any { it.email.equals(newUser.email, ignoreCase = true) || it.roleSpecificId == newUser.roleSpecificId }) {
            return false // duplicate
        }
        _users.value = _users.value + newUser
        setCurrentUser(newUser)
        logActivity(newUser, "Account Registration", "New account registered as ${newUser.role.label}.")
        return true
    }

    fun markStudentAttendance(
        student: UserAccount,
        subjectCode: String,
        subjectName: String,
        timeSlot: String,
        teacherName: String
    ): Boolean {
        val today = dateFormat.format(Date())
        // Check duplicate
        val existing = _attendanceRecords.value.find {
            it.studentId == student.roleSpecificId &&
            it.subjectCode == subjectCode &&
            it.date == today
        }
        if (existing != null) {
            return false // Already marked!
        }

        val newRecord = AttendanceRecord(
            id = "ATT-${System.currentTimeMillis().toString().takeLast(5)}",
            studentId = student.roleSpecificId,
            studentName = student.fullName,
            rollNumber = student.rollNumber,
            department = student.department,
            subjectCode = subjectCode,
            subjectName = subjectName,
            date = today,
            timeSlot = timeSlot,
            teacherName = teacherName,
            status = AttendanceStatus.PRESENT,
            isLocked = true, // Lock immediately upon student submission
            markedAtTime = timeFormat.format(Date()),
            lastModifiedBy = "${student.fullName} (Student)"
        )

        _attendanceRecords.value = listOf(newRecord) + _attendanceRecords.value
        logActivity(
            student,
            "Attendance Locked & Marked",
            "Marked PRESENT for $subjectCode ($subjectName). Record locked."
        )
        return true
    }

    fun coeUpdateAttendance(
        recordId: String,
        newStatus: AttendanceStatus,
        modifierUser: UserAccount
    ) {
        _attendanceRecords.value = _attendanceRecords.value.map { rec ->
            if (rec.id == recordId) {
                rec.copy(
                    status = newStatus,
                    lastModifiedBy = "${modifierUser.fullName} (COE)"
                )
            } else rec
        }
        logActivity(
            modifierUser,
            "Attendance Correction",
            "COE updated record $recordId to ${newStatus.label}."
        )
    }

    fun coeDeleteAttendance(recordId: String, modifierUser: UserAccount) {
        _attendanceRecords.value = _attendanceRecords.value.filter { it.id != recordId }
        logActivity(
            modifierUser,
            "Attendance Record Deleted",
            "COE removed attendance record $recordId."
        )
    }

    fun coeToggleLockStatus(recordId: String, modifierUser: UserAccount) {
        _attendanceRecords.value = _attendanceRecords.value.map { rec ->
            if (rec.id == recordId) {
                rec.copy(
                    isLocked = !rec.isLocked,
                    lastModifiedBy = "${modifierUser.fullName} (COE)"
                )
            } else rec
        }
    }

    fun adminAddUser(newUser: UserAccount, adminUser: UserAccount) {
        _users.value = _users.value + newUser
        logActivity(adminUser, "Admin User Creation", "Admin created new user ${newUser.fullName} (${newUser.role.label}).")
    }

    fun adminUpdateUser(updatedUser: UserAccount, adminUser: UserAccount) {
        _users.value = _users.value.map { if (it.id == updatedUser.id) updatedUser else it }
        logActivity(adminUser, "Admin User Edit", "Admin updated details for ${updatedUser.fullName}.")
    }

    fun adminToggleUserStatus(userId: String, adminUser: UserAccount) {
        _users.value = _users.value.map { u ->
            if (u.id == userId) {
                val newStatus = if (u.status == UserStatus.ACTIVE) UserStatus.SUSPENDED else UserStatus.ACTIVE
                logActivity(adminUser, "User Status Change", "User ${u.fullName} changed to ${newStatus.label}.")
                u.copy(status = newStatus)
            } else u
        }
    }

    fun adminDeleteUser(userId: String, adminUser: UserAccount) {
        val u = _users.value.find { it.id == userId }
        if (u != null) {
            _users.value = _users.value.filter { it.id != userId }
            logActivity(adminUser, "User Deleted", "User ${u.fullName} was permanently deleted by Admin.")
        }
    }

    fun adminResetPassword(userId: String, newPass: String, adminUser: UserAccount) {
        _users.value = _users.value.map { u ->
            if (u.id == userId) {
                logActivity(adminUser, "Password Reset", "Admin reset password for user ${u.fullName}.")
                u.copy(passwordHash = newPass)
            } else u
        }
    }

    fun updateUserProfile(userId: String, name: String, phone: String, dept: String) {
        _users.value = _users.value.map { u ->
            if (u.id == userId) {
                val updated = u.copy(fullName = name, phone = phone, department = dept)
                if (_currentUser.value?.id == userId) {
                    _currentUser.value = updated
                }
                updated
            } else u
        }
    }

    fun changeUserPassword(userId: String, oldPass: String, newPass: String): Boolean {
        val u = _users.value.find { it.id == userId }
        if (u != null && u.passwordHash == oldPass) {
            _users.value = _users.value.map { if (it.id == userId) it.copy(passwordHash = newPass) else it }
            if (_currentUser.value?.id == userId) {
                _currentUser.value = _currentUser.value?.copy(passwordHash = newPass)
            }
            return true
        }
        return false
    }

    private fun logActivity(user: UserAccount, action: String, details: String) {
        val newLog = ActivityLogItem(
            id = "LOG-${System.currentTimeMillis().toString().takeLast(6)}",
            timestamp = dateTimeFormat.format(Date()),
            userId = user.roleSpecificId,
            userName = user.fullName,
            userRole = user.role,
            action = action,
            details = details
        )
        _activityLogs.value = listOf(newLog) + _activityLogs.value
    }
}
