package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.CompletedWorkout
import com.example.data.model.JoggingPlan
import com.example.data.model.JoggingSession
import com.example.data.model.PhaseType
import com.example.data.model.UserSettings
import com.example.ui.theme.*
import com.example.ui.viewmodel.WorkoutViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MainDashboard(viewModel: WorkoutViewModel) {
    var currentTab by remember { mutableStateOf(0) }
    val isWorkoutActive by viewModel.isWorkoutActive.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (!isWorkoutActive) {
                NavigationBar(
                    containerColor = BoldNavBackground,
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .height(80.dp)
                        .border(1.dp, Color(0xFFDDE2EA), RoundedCornerShape(0.dp))
                ) {
                    NavigationBarItem(
                        selected = currentTab == 0,
                        onClick = { currentTab = 0 },
                        icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Plan") },
                        label = { Text("Plan", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BoldPrimary,
                            selectedTextColor = BoldText,
                            unselectedIconColor = BoldSubdued,
                            unselectedTextColor = BoldSubdued,
                            indicatorColor = BoldSecondary
                        ),
                        modifier = Modifier.testTag("tab_schedule")
                    )
                    NavigationBarItem(
                        selected = currentTab == 1,
                        onClick = { currentTab = 1 },
                        icon = { Icon(Icons.Default.BarChart, contentDescription = "Stats") },
                        label = { Text("Stats", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BoldPrimary,
                            selectedTextColor = BoldText,
                            unselectedIconColor = BoldSubdued,
                            unselectedTextColor = BoldSubdued,
                            indicatorColor = BoldSecondary
                        ),
                        modifier = Modifier.testTag("tab_progress")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BoldBackground)
                .padding(innerPadding)
        ) {
            if (isWorkoutActive) {
                ActiveCoachScreen(viewModel = viewModel)
            } else {
                when (currentTab) {
                    0 -> ScheduleScreen(viewModel = viewModel)
                    1 -> ProgressScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun ScheduleScreen(viewModel: WorkoutViewModel) {
    val settings by viewModel.userSettings.collectAsState()
    val completedWorkouts by viewModel.completedWorkouts.collectAsState()
    var showPersonalizer by remember { mutableStateOf(false) }
    var expandedWeek by remember { mutableStateOf<Int?>(null) }

    val currentWeek = settings.currentWeek
    val displayWeek = expandedWeek ?: currentWeek

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // App Bar / Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "COUCH TO 5K",
                    style = MaterialTheme.typography.labelSmall,
                    color = BoldSubdued,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Welcome, Alex",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
                    fontWeight = FontWeight.Bold,
                    color = BoldText
                )
            }
            IconButton(
                onClick = { showPersonalizer = true },
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFDDE2EA), CircleShape)
                    .border(2.dp, Color.White, CircleShape)
                    .testTag("settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Personalize Plan",
                    tint = BoldText,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Weekly Progress Card (As requested in design HTML spec)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showPersonalizer = true },
            colors = CardDefaults.cardColors(containerColor = BoldSurface),
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Week $displayWeek of 8",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = BoldText
                    )
                    val progressPct = ((displayWeek - 1) * 100 / 8) + 12
                    Text(
                        text = "$progressPct% Complete",
                        style = MaterialTheme.typography.bodyMedium,
                        color = BoldSubdued,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                // Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape)
                        .background(BoldTrack)
                ) {
                    val progressFrac = ((displayWeek - 1).toFloat() / 8f).coerceIn(0.12f, 1f)
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progressFrac)
                            .clip(CircleShape)
                            .background(BoldPrimary)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "You're doing great! Today's session focuses on building endurance with longer customized intervals.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BoldSubdued,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Training Plan Header Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sessions List",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = BoldText
            )
            // Cycle Week Dropdown Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(BoldSurfaceLight)
                    .clickable {
                        val next = if (displayWeek >= 8) 1 else displayWeek + 1
                        expandedWeek = next
                    }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Week $displayWeek of 8",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = BoldPrimary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Cycle Week",
                    tint = BoldPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Week sessions list
        val sessions = JoggingPlan.weeksList[displayWeek] ?: emptyList()
        val preferredDaysList = settings.preferredDays.split(", ").filter { it.isNotEmpty() }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sessions) { session ->
                val dayOfWeekLabel = preferredDaysList.getOrNull(session.sessionIndex - 1) ?: "Day ${session.sessionIndex}"
                val isCompleted = completedWorkouts.any { it.week == displayWeek && it.sessionIndex == session.sessionIndex }

                SessionCard(
                    session = session,
                    dayLabel = dayOfWeekLabel,
                    level = settings.joggingLevel,
                    isCompleted = isCompleted,
                    onStart = {
                        val customized = viewModel.getCustomizedSession(displayWeek, session.sessionIndex, settings.joggingLevel)
                        if (customized != null) {
                            viewModel.startWorkout(customized)
                        }
                    }
                )
            }
        }
    }

    if (showPersonalizer) {
        PersonalizerDialog(
            currentSettings = settings,
            onDismiss = { showPersonalizer = false },
            onSave = { level, days, time, voiceEnabled, voicePacing, voiceStyle, voiceFreq ->
                viewModel.updateSchedulePreferences(level, days, time)
                viewModel.updateVoicePreferences(voiceEnabled, voicePacing, voiceStyle, voiceFreq)
                showPersonalizer = false
            }
        )
    }
}

@Composable
fun SessionCard(
    session: JoggingSession,
    dayLabel: String,
    level: String,
    isCompleted: Boolean,
    onStart: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("session_card_${session.week}_${session.sessionIndex}"),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) Color(0xFFF2F0F4) else Color(0xFFD3E4FF)
        ),
        shape = RoundedCornerShape(28.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isCompleted) Color(0xFFCAC4D0) else Color(0xFFA8C7FF))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isCompleted) "COMPLETED" else "TODAY'S SESSION",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isCompleted) BoldSubdued else Color(0xFF001D36),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${dayLabel.uppercase()} • SESSION ${session.sessionIndex}",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isCompleted) BoldSubdued else Color(0xFF001D36).copy(alpha = 0.8f),
                        fontWeight = FontWeight.Bold
                    )
                }

                if (isCompleted) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.6f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Completed",
                            tint = BoldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "DONE",
                            style = MaterialTheme.typography.labelSmall,
                            color = BoldPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Large, bold layout title matching theme
            val titleParts = session.title.uppercase().split(" AND ")
            val formattedTitle = if (titleParts.size >= 2) {
                "${titleParts[0]}\n${titleParts[1]}"
            } else {
                session.title.uppercase()
            }

            Text(
                text = formattedTitle,
                style = if (isCompleted) MaterialTheme.typography.titleLarge else MaterialTheme.typography.displayMedium.copy(fontSize = 32.sp),
                fontWeight = FontWeight.Black,
                color = if (isCompleted) BoldText else Color(0xFF001D36),
                lineHeight = 36.sp,
                letterSpacing = (-1).sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = session.description,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isCompleted) BoldSubdued else Color(0xFF001D36).copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Voice Guide banner (As requested in HTML mockup)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.4f))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(BoldPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Voice Guide",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "VOICE GUIDE",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isCompleted) BoldSubdued else Color(0xFF001D36).copy(alpha = 0.6f),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Coach Sarah • High Energy",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isCompleted) BoldText else Color(0xFF001D36),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                val estimatedMinutes = when (level) {
                    "Absolute Beginner" -> 22
                    "Re-starter" -> 32
                    else -> 28
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⏱ ~$estimatedMinutes mins total",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isCompleted) BoldSubdued else Color(0xFF001D36).copy(alpha = 0.7f),
                        fontWeight = FontWeight.Bold
                    )

                    Button(
                        onClick = onStart,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BoldPrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(32.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("start_session_${session.week}_${session.sessionIndex}")
                    ) {
                        Icon(
                            imageVector = if (isCompleted) Icons.Default.Refresh else Icons.Default.PlayArrow,
                            contentDescription = "Start Coach",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isCompleted) "RE-RUN" else "START RUN",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BorderStroke(width: androidx.compose.ui.unit.Dp, color: Color) = Modifier.border(width, color, RoundedCornerShape(28.dp))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalizerDialog(
    currentSettings: UserSettings,
    onDismiss: () -> Unit,
    onSave: (String, List<String>, String, Boolean, Boolean, String, Int) -> Unit
) {
    val levels = listOf("Absolute Beginner", "Active Beginner", "Re-starter")
    var selectedLevel by remember { mutableStateOf(currentSettings.joggingLevel) }

    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    var selectedDays by remember {
        mutableStateOf(
            currentSettings.preferredDays.split(", ").filter { it.isNotEmpty() }.toMutableList()
        )
    }

    val times = listOf("06:00 AM", "07:00 AM", "07:30 AM", "08:00 AM", "05:30 PM", "06:30 PM")
    var selectedTime by remember { mutableStateOf(currentSettings.preferredTime) }

    // Voice Coach State variables
    var voiceCoachEnabled by remember { mutableStateOf(currentSettings.voiceCoachEnabled) }
    var voicePacingEnabled by remember { mutableStateOf(currentSettings.voicePacingEnabled) }
    var selectedStyle by remember { mutableStateOf(currentSettings.voiceCoachStyle) }
    var selectedFrequency by remember { mutableStateOf(currentSettings.coachingFrequencySeconds) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("personalizer_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Personalize Plan",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = BoldText
                )

                // 1. Intensity selector
                Column {
                    Text(
                        text = "Jogging Intensity Level",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = BoldSubdued
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        levels.forEach { level ->
                            val isSelected = selectedLevel == level
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) BoldPrimary else BoldSurfaceLight)
                                    .clickable { selectedLevel = level }
                                    .padding(vertical = 10.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = level,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else BoldText,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // 2. Schedule Selector
                Column {
                    Text(
                        text = "Preferred Days (Select 3)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = BoldSubdued
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        daysOfWeek.forEach { day ->
                            val isSelected = selectedDays.contains(day)
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) BoldPrimary else BoldSurfaceLight)
                                    .clickable {
                                        val newList = selectedDays.toMutableList()
                                        if (newList.contains(day)) {
                                            newList.remove(day)
                                        } else {
                                            newList.add(day)
                                        }
                                        selectedDays = newList
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.take(2),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = if (isSelected) Color.White else BoldText
                                )
                            }
                        }
                    }
                }

                // 3. Time selector
                Column {
                    Text(
                        text = "Target Time",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = BoldSubdued
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        times.take(3).forEach { time ->
                            val isSelected = selectedTime == time
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) BoldPrimary else BoldSurfaceLight)
                                    .clickable { selectedTime = time }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = time,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else BoldText
                                )
                            }
                        }
                    }
                }

                // Divider
                Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(Color(0xFFCAC4D0)))

                // Voice Coach Settings Header
                Text(
                    text = "VOICE COACH OPTIONS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = BoldPrimary,
                    letterSpacing = 1.sp
                )

                // Voice Coach Switch Toggle Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Audio Guidance (TTS)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = BoldText
                        )
                        Text(
                            text = "Automated vocal prompts for running/walking instructions",
                            fontSize = 11.sp,
                            color = BoldSubdued,
                            lineHeight = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(BoldSurfaceLight)
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (voiceCoachEnabled) BoldPrimary else Color.Transparent)
                                .clickable { voiceCoachEnabled = true }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "ON",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (voiceCoachEnabled) Color.White else BoldText
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (!voiceCoachEnabled) BoldCoral else Color.Transparent)
                                .clickable { voiceCoachEnabled = false }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "OFF",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (!voiceCoachEnabled) Color.White else BoldText
                            )
                        }
                    }
                }

                // Pacing Audio Tips Switch Toggle Row (Only visible if Voice Coach is enabled)
                if (voiceCoachEnabled) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Pacing Alerts & Tips",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = BoldText
                            )
                            Text(
                                text = "Speak cadence, form and pacing checks periodically",
                                fontSize = 11.sp,
                                color = BoldSubdued,
                                lineHeight = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(BoldSurfaceLight)
                                .padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (voicePacingEnabled) BoldPrimary else Color.Transparent)
                                    .clickable { voicePacingEnabled = true }
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "ON",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (voicePacingEnabled) Color.White else BoldText
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (!voicePacingEnabled) BoldCoral else Color.Transparent)
                                    .clickable { voicePacingEnabled = false }
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "OFF",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (!voicePacingEnabled) Color.White else BoldText
                                )
                            }
                        }
                    }

                    // Voice Coach Style Selector
                    Column {
                        Text(
                            text = "Choose Coach Persona",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = BoldSubdued
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        val styles = listOf("Sarah (High Energy)", "Coach John (Calm & Steady)", "Robot Coach (Precise)")
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            styles.forEach { styleOpt ->
                                val isSelected = selectedStyle == styleOpt
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) BoldPrimary else BoldSurfaceLight)
                                        .clickable { selectedStyle = styleOpt }
                                        .padding(vertical = 10.dp, horizontal = 12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = styleOpt,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else BoldText
                                        )
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Selected",
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Coaching Frequency Selector (If pacing enabled)
                    if (voicePacingEnabled) {
                        Column {
                            Text(
                                text = "Coaching Alert Frequency",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = BoldSubdued
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            val frequencies = listOf(
                                "Every 30s" to 30,
                                "Every 60s" to 60,
                                "Every 90s" to 90
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                frequencies.forEach { (label, value) ->
                                    val isSelected = selectedFrequency == value
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isSelected) BoldPrimary else BoldSurfaceLight)
                                            .clickable { selectedFrequency = value }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else BoldText
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCAC4D0)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BoldSubdued),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text("Cancel", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val daysToSave = if (selectedDays.isEmpty()) listOf("Mon", "Wed", "Fri") else selectedDays
                            onSave(
                                selectedLevel,
                                daysToSave,
                                selectedTime,
                                voiceCoachEnabled,
                                voicePacingEnabled,
                                selectedStyle,
                                selectedFrequency
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BoldPrimary, contentColor = Color.White),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.testTag("save_preferences_button")
                    ) {
                        Text("Apply", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ActiveCoachScreen(viewModel: WorkoutViewModel) {
    val session by viewModel.activeSession.collectAsState()
    val currentPhaseIdx by viewModel.currentPhaseIndex.collectAsState()
    val secondsRemaining by viewModel.phaseSecondsRemaining.collectAsState()
    val secondsElapsed by viewModel.phaseSecondsElapsed.collectAsState()
    val isPaused by viewModel.isPaused.collectAsState()
    val spokenText by viewModel.spokenText.collectAsState()

    val runSeconds by viewModel.workoutRunSeconds.collectAsState()
    val durationSeconds by viewModel.workoutDurationSeconds.collectAsState()
    val distanceKm by viewModel.workoutDistanceKm.collectAsState()
    val calories by viewModel.workoutCalories.collectAsState()

    val currentSession = session ?: return
    val phases = currentSession.phases
    val currentPhase = phases.getOrNull(currentPhaseIdx) ?: return

    val totalPhaseDuration = currentPhase.durationSeconds
    val progressFraction = if (totalPhaseDuration > 0) {
        (secondsElapsed.toFloat() / totalPhaseDuration).coerceIn(0f, 1f)
    } else {
        0f
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BoldBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "WEEK ${currentSession.week} • SESSION ${currentSession.sessionIndex}".uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = BoldPrimary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = currentSession.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = BoldText,
                textAlign = TextAlign.Center
            )
        }

        // Timer Circle coaching meter (High contrast and beautiful)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(240.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = Color(0xFFDDE2EA),
                        style = Stroke(width = 16.dp.toPx())
                    )
                }

                val activeColor = when (currentPhase.type) {
                    PhaseType.RUN -> BoldCoral
                    PhaseType.WALK -> BoldPrimary
                    else -> Color(0xFFFBBF24) // Gold for Warmup / Cooldown
                }

                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = activeColor,
                        startAngle = -90f,
                        sweepAngle = 360f * progressFraction,
                        useCenter = false,
                        style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = currentPhase.type.name,
                        style = MaterialTheme.typography.labelLarge,
                        color = activeColor,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatMMSS(secondsRemaining),
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Black,
                        color = BoldText
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "of ${formatMMSS(totalPhaseDuration)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = BoldSubdued,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Coach spoken subtitles / coaching messages container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF2F0F4))
                    .border(1.dp, Color(0xFFCAC4D0), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Speaker",
                        tint = BoldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = spokenText.ifEmpty { currentPhase.message },
                        style = MaterialTheme.typography.bodyMedium,
                        color = BoldText,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Live stats grid (Two column stats rows for Bold look)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Timer, contentDescription = "Duration", tint = BoldPrimary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = formatMMSS(durationSeconds), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = BoldText)
                Text(text = "Elapsed", style = MaterialTheme.typography.labelSmall, color = BoldSubdued, fontWeight = FontWeight.Bold)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.DirectionsRun, contentDescription = "Distance", tint = BoldPrimary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = String.format("%.2f km", distanceKm), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = BoldText)
                Text(text = "Distance", style = MaterialTheme.typography.labelSmall, color = BoldSubdued, fontWeight = FontWeight.Bold)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.LocalFireDepartment, contentDescription = "Calories", tint = BoldPrimary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "$calories kcal", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = BoldText)
                Text(text = "Calories", style = MaterialTheme.typography.labelSmall, color = BoldSubdued, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Large high-contrast media play/pause controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // STOP Button
            IconButton(
                onClick = { viewModel.finishWorkout(completedSuccessfully = false) },
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF2F0F4))
                    .border(1.dp, Color(0xFFCAC4D0), CircleShape)
                    .testTag("stop_workout_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "Stop workout",
                    tint = Color.Red,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(28.dp))

            // PLAY/PAUSE Button
            IconButton(
                onClick = {
                    if (isPaused) viewModel.resumeWorkout() else viewModel.pauseWorkout()
                },
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(BoldPrimary)
                    .shadow(8.dp)
                    .testTag("play_pause_workout_button")
            ) {
                Icon(
                    imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                    contentDescription = if (isPaused) "Resume" else "Pause",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.width(28.dp))

            // SKIP Button
            IconButton(
                onClick = { viewModel.skipPhase() },
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF2F0F4))
                    .border(1.dp, Color(0xFFCAC4D0), CircleShape)
                    .testTag("skip_phase_button")
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Skip phase",
                    tint = BoldText,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun ProgressScreen(viewModel: WorkoutViewModel) {
    val completedList by viewModel.completedWorkouts.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "PROGRESS STATISTICS",
            style = MaterialTheme.typography.labelSmall,
            color = BoldPrimary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "Your Running Journey",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            color = BoldText,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Stats Grid Cards in exact Bold Typography style
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val totalRuns = completedList.size
            val totalDist = completedList.sumOf { it.distanceKm }
            val totalTimeMin = completedList.sumOf { it.durationSeconds } / 60

            StatMetricCard(
                title = "Total Runs",
                value = "$totalRuns",
                unit = "",
                modifier = Modifier.weight(1f)
            )
            StatMetricCard(
                title = "Total Distance",
                value = String.format("%.1f", totalDist),
                unit = "km",
                modifier = Modifier.weight(1f)
            )
            StatMetricCard(
                title = "Active Time",
                value = "$totalTimeMin",
                unit = "mins",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Progress Chart
        ProgressChart(workouts = completedList)

        Spacer(modifier = Modifier.height(16.dp))

        // History List Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Workout History",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = BoldText
            )
            if (completedList.isNotEmpty()) {
                Text(
                    text = "Clear All",
                    style = MaterialTheme.typography.labelLarge,
                    color = BoldCoral,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { viewModel.clearHistory() }
                        .padding(4.dp)
                        .testTag("clear_history_button")
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // History List
        if (completedList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No runs logged yet. Put your shoes on!",
                    color = BoldSubdued,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(completedList) { workout ->
                    HistoryItemCard(
                        workout = workout,
                        onDelete = { viewModel.deleteWorkout(workout.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun StatMetricCard(
    title: String,
    value: String,
    unit: String = "",
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F0F4)),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCAC4D0))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF49454F),
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 28.sp),
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1D1B20)
                )
                if (unit.isNotEmpty()) {
                    Text(
                        text = unit,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF49454F),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ProgressChart(workouts: List<CompletedWorkout>) {
    if (workouts.isEmpty()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F0F4)),
            shape = RoundedCornerShape(24.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCAC4D0))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsRun,
                    contentDescription = "No run history",
                    tint = BoldPrimary.copy(alpha = 0.5f),
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Weekly Activity Trend",
                    fontWeight = FontWeight.Black,
                    color = BoldText,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "Complete runs to visualize your active mileage here!",
                    fontSize = 12.sp,
                    color = BoldSubdued,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
        return
    }

    val chartData = workouts.take(7).reversed()
    val maxDistance = chartData.maxOfOrNull { it.distanceKm }?.coerceAtLeast(1.0) ?: 1.0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F0F4)),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCAC4D0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Last 7 Runs (Distance trend)",
                style = MaterialTheme.typography.titleSmall,
                color = BoldText,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(12.dp))
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.0f)
            ) {
                val width = size.width
                val height = size.height
                val paddingLeft = 30f
                val paddingBottom = 30f
                val chartWidth = width - paddingLeft
                val chartHeight = height - paddingBottom

                val gridLines = 3
                for (i in 0..gridLines) {
                    val y = chartHeight - (chartHeight / gridLines) * i
                    drawLine(
                        color = Color.Black.copy(alpha = 0.05f),
                        start = androidx.compose.ui.geometry.Offset(paddingLeft, y),
                        end = androidx.compose.ui.geometry.Offset(width, y),
                        strokeWidth = 2f
                    )
                }

                if (chartData.size == 1) {
                    val x = paddingLeft + chartWidth / 2f
                    val y = chartHeight - (chartData[0].distanceKm.toFloat() / maxDistance.toFloat()) * chartHeight
                    drawCircle(
                        color = BoldPrimary,
                        radius = 12f,
                        center = androidx.compose.ui.geometry.Offset(x, y)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 6f,
                        center = androidx.compose.ui.geometry.Offset(x, y)
                    )
                } else {
                    val points = chartData.mapIndexed { index, workout ->
                        val x = paddingLeft + (chartWidth / (chartData.size - 1)) * index
                        val y = chartHeight - (workout.distanceKm.toFloat() / maxDistance.toFloat()) * chartHeight
                        androidx.compose.ui.geometry.Offset(x, y)
                    }

                    val fillPath = androidx.compose.ui.graphics.Path().apply {
                        moveTo(points[0].x, points[0].y)
                        for (i in 1 until points.size) {
                            lineTo(points[i].x, points[i].y)
                        }
                        lineTo(points.last().x, chartHeight)
                        lineTo(points.first().x, chartHeight)
                        close()
                    }
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(BoldPrimary.copy(alpha = 0.3f), Color.Transparent),
                            startY = points.minOf { it.y },
                            endY = chartHeight
                        )
                    )

                    val strokePath = androidx.compose.ui.graphics.Path().apply {
                        moveTo(points[0].x, points[0].y)
                        for (i in 1 until points.size) {
                            lineTo(points[i].x, points[i].y)
                        }
                    }
                    drawPath(
                        path = strokePath,
                        color = BoldPrimary,
                        style = Stroke(width = 6f, cap = StrokeCap.Round)
                    )

                    points.forEach { pt ->
                        drawCircle(
                            color = BoldPrimary,
                            radius = 8f,
                            center = pt
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 4f,
                            center = pt
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryItemCard(workout: CompletedWorkout, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F0F4)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCAC4D0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(BoldSecondary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsRun,
                        contentDescription = "Run log",
                        tint = BoldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Week ${workout.week} Session ${workout.sessionIndex}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Black,
                        color = BoldText
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = String.format("%.2f km", workout.distanceKm),
                            fontSize = 12.sp,
                            color = BoldPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "•",
                            fontSize = 12.sp,
                            color = BoldSubdued
                        )
                        Text(
                            text = "${workout.durationSeconds / 60}m ${workout.durationSeconds % 60}s",
                            fontSize = 12.sp,
                            color = BoldSubdued,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "•",
                            fontSize = 12.sp,
                            color = BoldSubdued
                        )
                        val formattedDate = remember(workout.timestamp) {
                            try {
                                SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(workout.timestamp))
                            } catch (e: Exception) {
                                ""
                            }
                        }
                        Text(
                            text = formattedDate,
                            fontSize = 12.sp,
                            color = BoldSubdued,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.testTag("delete_workout_button_${workout.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete run log",
                    tint = BoldCoral,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

fun formatMMSS(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return String.format("%02d:%02d", m, s)
}
