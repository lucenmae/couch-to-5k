package com.example.data.repository

import com.example.data.database.WorkoutDao
import com.example.data.model.CompletedWorkout
import com.example.data.model.JoggingPlan
import com.example.data.model.JoggingSession
import com.example.data.model.UserSettings
import kotlinx.coroutines.flow.Flow

class WorkoutRepository(private val workoutDao: WorkoutDao) {
    val allCompletedWorkouts: Flow<List<CompletedWorkout>> = workoutDao.getAllCompletedWorkouts()
    val userSettings: Flow<UserSettings?> = workoutDao.getUserSettings()

    suspend fun saveCompletedWorkout(workout: CompletedWorkout) {
        workoutDao.insertCompletedWorkout(workout)
    }

    suspend fun deleteCompletedWorkout(id: Int) {
        workoutDao.deleteCompletedWorkoutById(id)
    }

    suspend fun clearCompletedWorkouts() {
        workoutDao.clearAllCompletedWorkouts()
    }

    suspend fun saveUserSettings(settings: UserSettings) {
        workoutDao.saveUserSettings(settings)
    }

    fun getCustomizedSession(week: Int, sessionIndex: Int, level: String): JoggingSession? {
        val weekSessions = JoggingPlan.weeksList[week] ?: return null
        val session = weekSessions.firstOrNull { it.sessionIndex == sessionIndex } ?: return null
        val customizedPhases = JoggingPlan.getCustomizedPhases(session.phases, level)
        return session.copy(phases = customizedPhases)
    }
}
