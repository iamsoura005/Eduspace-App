package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldSuccess
import com.example.utils.Responsive

data class WeeklyClassItem(
    val id: String,
    val dayOfWeek: String, // "Mon", "Tue", etc.
    val timeSlot: String,  // "09:00 AM - 10:30 AM"
    val subjectCode: String,
    val subjectName: String,
    val classType: String, // "Lecture", "Lab", "Seminar"
    val roomLocation: String,
    val professorName: String,
    val isLive: Boolean = false,
    val isCompleted: Boolean = false,
    var isBookmarked: Boolean = false
)

@Composable
fun TimetableComponent(
    modifier: Modifier = Modifier,
    onClassClick: ((WeeklyClassItem) -> Unit)? = null
) {
    val dimensions = Responsive.dimensions

    var selectedDayIndex by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedTypeFilter by remember { mutableStateOf("All") }

    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val dayDates = listOf("Oct 19", "Oct 20", "Oct 21", "Oct 22", "Oct 23", "Oct 24")
    val typeFilters = listOf("All", "Lecture", "Lab", "Seminar")

    // Full Weekly Schedule Data
    val sampleSchedule = remember {
        listOf(
            // Monday
            WeeklyClassItem("1", "Mon", "09:00 AM - 10:30 AM", "MATH204", "Linear Algebra & Optimization", "Lecture", "Hall C • Floor 2", "Prof. Elena Rostova", isCompleted = true),
            WeeklyClassItem("2", "Mon", "11:00 AM - 12:30 PM", "CS301", "Algorithms & Data Structures", "Lab", "Lab 4B • Computer Block", "Dr. Robert Vance", isLive = true),
            WeeklyClassItem("3", "Mon", "02:00 PM - 03:30 PM", "SE402", "Mobile Application Architecture", "Lecture", "Studio 1 • Innovation Hub", "Dr. Michael Zhang"),
            WeeklyClassItem("4", "Mon", "04:00 PM - 05:30 PM", "CS301-L", "Algorithms Practical Lab Session", "Lab", "Lab 2A • Computer Block", "T.A. Sarah Jenkins"),

            // Tuesday
            WeeklyClassItem("5", "Tue", "09:30 AM - 11:00 AM", "PHY102", "Quantum Mechanics Fundamentals", "Lecture", "Hall B • Physics Wing", "Prof. Alan Turing"),
            WeeklyClassItem("6", "Tue", "01:00 PM - 02:30 PM", "ENG201", "Technical Writing & Research", "Seminar", "Room 302 • Arts Building", "Dr. Clara Oswald"),

            // Wednesday
            WeeklyClassItem("7", "Wed", "09:00 AM - 10:30 AM", "MATH204", "Linear Algebra & Optimization", "Lecture", "Hall C • Floor 2", "Prof. Elena Rostova"),
            WeeklyClassItem("8", "Wed", "11:30 AM - 01:00 PM", "CS305", "Database Systems Engineering", "Lab", "Lab 1A • Tech Hub", "Dr. David Kim"),

            // Thursday
            WeeklyClassItem("9", "Thu", "10:00 AM - 11:30 AM", "SE402", "Mobile Application Architecture", "Lecture", "Studio 1 • Innovation Hub", "Dr. Michael Zhang"),
            WeeklyClassItem("10", "Thu", "02:00 PM - 04:00 PM", "CS305-L", "Database Systems Lab", "Lab", "Lab 1A • Tech Hub", "T.A. Alex Mercer"),

            // Friday
            WeeklyClassItem("11", "Fri", "09:00 AM - 11:00 AM", "CS301", "Algorithms Advanced Seminar", "Seminar", "Auditorium A", "Dr. Robert Vance"),
            WeeklyClassItem("12", "Fri", "01:30 PM - 03:00 PM", "AI401", "Artificial Intelligence & Ethics", "Lecture", "Hall D • AI Wing", "Prof. Sophia Chen"),

            // Saturday
            WeeklyClassItem("13", "Sat", "10:00 AM - 12:00 PM", "PROJ300", "Capstone Project Mentorship", "Lab", "Innovation Center 4", "Dr. Michael Zhang")
        )
    }

    val selectedDay = days[selectedDayIndex]

    val filteredSchedule = sampleSchedule.filter { item ->
        item.dayOfWeek == selectedDay &&
                (selectedTypeFilter == "All" || item.classType.equals(selectedTypeFilter, ignoreCase = true)) &&
                (searchQuery.isEmpty() ||
                        item.subjectCode.contains(searchQuery, ignoreCase = true) ||
                        item.subjectName.contains(searchQuery, ignoreCase = true) ||
                        item.professorName.contains(searchQuery, ignoreCase = true) ||
                        item.roomLocation.contains(searchQuery, ignoreCase = true))
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("timetable_component"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Search & Filter Header Row
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search course, code, or professor...") },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("timetable_search_input"),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )

        // Day Selector Pills Bar (Immersive UI Style)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(days.size) { index ->
                val isSelected = index == selectedDayIndex
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { selectedDayIndex = index }
                        .testTag("timetable_day_tab_$index"),
                    color = if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surface,
                    border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 18.dp, vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = days[index],
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = dayDates[index],
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Filter Type Chips (Lecture / Lab / Seminar)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(typeFilters) { filter ->
                FilterChip(
                    selected = filter == selectedTypeFilter,
                    onClick = { selectedTypeFilter = filter },
                    label = { Text(filter, fontSize = 12.sp) },
                    modifier = Modifier.testTag("timetable_filter_$filter")
                )
            }
        }

        // Class Schedule List
        if (filteredSchedule.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Class,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No classes scheduled for $selectedDay",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                filteredSchedule.forEach { classItem ->
                    TimetableClassCard(
                        classItem = classItem,
                        onClick = { onClassClick?.invoke(classItem) }
                    )
                }
            }
        }
    }
}

@Composable
fun TimetableClassCard(
    classItem: WeeklyClassItem,
    onClick: () -> Unit
) {
    val dimensions = Responsive.dimensions
    var isBookmarkedState by remember { mutableStateOf(classItem.isBookmarked) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimensions.cardCornerRadius))
            .clickable { onClick() }
            .testTag("class_card_${classItem.id}"),
        shape = RoundedCornerShape(dimensions.cardCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = if (classItem.isLive) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            else MaterialTheme.colorScheme.surface
        ),
        border = if (classItem.isLive) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
        else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Subject Code, Class Type Badge, Live Status, Bookmark
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = classItem.subjectCode,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = classItem.classType,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (classItem.isLive) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = EmeraldSuccess
                        ) {
                            Text(
                                text = "LIVE NOW",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    } else if (classItem.isCompleted) {
                        Text(
                            text = "Completed",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }

                    IconButton(
                        onClick = { isBookmarkedState = !isBookmarkedState },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isBookmarkedState) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark Class",
                            tint = if (isBookmarkedState) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Class Title
            Text(
                text = classItem.subjectName,
                fontSize = dimensions.responsiveSp(16f),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Time, Room, and Professor Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = classItem.timeSlot,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = classItem.roomLocation,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = classItem.professorName,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
