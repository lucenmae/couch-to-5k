package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "completed_workouts")
data class CompletedWorkout(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val week: Int,
    val sessionIndex: Int,
    val durationSeconds: Int,
    val runSeconds: Int,
    val distanceKm: Double,
    val calories: Int,
    val difficultyRating: Int
)

@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey val id: Int = 1, // Single row configuration
    val joggingLevel: String = "Active Beginner", // "Absolute Beginner", "Active Beginner", "Re-starter"
    val preferredDays: String = "Mon, Wed, Fri", // Comma-separated
    val preferredTime: String = "07:30 AM",
    val currentWeek: Int = 1,
    val voiceCoachEnabled: Boolean = true,
    val voicePacingEnabled: Boolean = true,
    val voiceCoachStyle: String = "Sarah (High Energy)", // Sarah, John, Precise
    val coachingFrequencySeconds: Int = 60 // Every 60 seconds pacing/interval voice cues
)
