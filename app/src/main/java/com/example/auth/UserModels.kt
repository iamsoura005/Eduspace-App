package com.example.auth

enum class UserRole(val label: String) {
    STUDENT("Student"),
    COE("COE (Controller of Exams)"),
    IT_ADMIN("IT Administration")
}

enum class UserStatus(val label: String) {
    ACTIVE("Active"),
    SUSPENDED("Suspended")
}

data class UserAccount(
    val id: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val role: UserRole,
    val department: String,
    val roleSpecificId: String, // Student ID / Employee ID / Admin ID
    val rollNumber: String = "", // For Student
    val semester: String = "",   // For Student
    val status: UserStatus = UserStatus.ACTIVE,
    val passwordHash: String = "123456",
    val profileImageRes: Int? = null
)

enum class AttendanceStatus(val label: String, val badgeColorHex: Long) {
    PRESENT("Present", 0xFF81C784),
    ABSENT("Absent", 0xFFE57373),
    LATE("Late", 0xFFFFB74D),
    EXCUSED("Excused", 0xFF64B5F6)
}

data class AttendanceRecord(
    val id: String,
    val studentId: String,
    val studentName: String,
    val rollNumber: String,
    val department: String,
    val subjectCode: String,
    val subjectName: String,
    val date: String, // e.g., "2026-08-07"
    val timeSlot: String, // e.g., "09:00 AM - 10:30 AM"
    val teacherName: String,
    var status: AttendanceStatus,
    var isLocked: Boolean = true, // Once marked by student, it locks immediately
    var markedAtTime: String = "",
    var lastModifiedBy: String = "System"
)

data class ActivityLogItem(
    val id: String,
    val timestamp: String,
    val userId: String,
    val userName: String,
    val userRole: UserRole,
    val action: String,
    val details: String
)
