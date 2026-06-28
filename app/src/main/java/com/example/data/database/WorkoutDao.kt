package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.CompletedWorkout
import com.example.data.model.UserSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM completed_workouts ORDER BY timestamp DESC")
    fun getAllCompletedWorkouts(): Flow<List<CompletedWorkout>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletedWorkout(workout: CompletedWorkout)

    @Query("DELETE FROM completed_workouts WHERE id = :id")
    suspend fun deleteCompletedWorkoutById(id: Int)

    @Query("DELETE FROM completed_workouts")
    suspend fun clearAllCompletedWorkouts()

    @Query("SELECT * FROM user_settings WHERE id = 1 LIMIT 1")
    fun getUserSettings(): Flow<UserSettings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserSettings(settings: UserSettings)
}
