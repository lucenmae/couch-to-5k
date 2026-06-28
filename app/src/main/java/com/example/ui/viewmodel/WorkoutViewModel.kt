package com.example.ui.viewmodel

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.CompletedWorkout
import com.example.data.model.JoggingSession
import com.example.data.model.JoggingPlan
import com.example.data.model.PhaseType
import com.example.data.model.UserSettings
import com.example.data.repository.WorkoutRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val repository = WorkoutRepository(db.workoutDao())

    // UI States observed from Database
    val userSettings: StateFlow<UserSettings> = repository.userSettings
        .map { it ?: UserSettings() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserSettings()
        )

    val completedWorkouts: StateFlow<List<CompletedWorkout>> = repository.allCompletedWorkouts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Active run tracking states
    private val _isWorkoutActive = MutableStateFlow(false)
    val isWorkoutActive: StateFlow<Boolean> = _isWorkoutActive.asStateFlow()

    private val _activeSession = MutableStateFlow<JoggingSession?>(null)
    val activeSession: StateFlow<JoggingSession?> = _activeSession.asStateFlow()

    private val _currentPhaseIndex = MutableStateFlow(0)
    val currentPhaseIndex: StateFlow<Int> = _currentPhaseIndex.asStateFlow()

    private val _phaseSecondsRemaining = MutableStateFlow(0)
    val phaseSecondsRemaining: StateFlow<Int> = _phaseSecondsRemaining.asStateFlow()

    private val _phaseSecondsElapsed = MutableStateFlow(0)
    val phaseSecondsElapsed: StateFlow<Int> = _phaseSecondsElapsed.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()

    private val _spokenText = MutableStateFlow("")
    val spokenText: StateFlow<String> = _spokenText.asStateFlow()

    // Real-time run telemetry
    val workoutRunSeconds = MutableStateFlow(0)
    val workoutDurationSeconds = MutableStateFlow(0)
    val workoutDistanceKm = MutableStateFlow(0.0)
    val workoutCalories = MutableStateFlow(0)

    // TextToSpeech Audio Coach
    private var tts: TextToSpeech? = null
    private val isTtsReady = MutableStateFlow(false)
    private var timerJob: Job? = null

    init {
        tts = TextToSpeech(application) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.getDefault()
                isTtsReady.value = true
            }
        }
    }

    fun speak(text: String) {
        val settings = userSettings.value
        if (!settings.voiceCoachEnabled) {
            _spokenText.value = text // Update subtitle text visually anyway
            return
        }

        val customizedText = when (settings.voiceCoachStyle) {
            "Coach John (Calm & Steady)" -> {
                text.replace("Let's crush this!", "Let us focus.")
                    .replace("outstanding work!", "well done.")
                    .replace("Put your shoes on!", "Prepare for your training.")
                    .replace("Congratulations! You completed today's session. Outstanding work!", "Congratulations. Today's session is now complete. Excellent effort.")
                    .replace("You are halfway through this phase. Keep breathing steady!", "Halfway mark reached. Maintain standard breathing rhythm.")
            }
            "Robot Coach (Precise)" -> {
                text.replace("Outstanding work!", "Task completed.")
                    .replace("Let's crush this!", "Initiating interval phase.")
                    .replace("Congratulations! You completed today's session. Outstanding work!", "Workout complete. All data streams successfully logged.")
                    .replace("You are halfway through this phase. Keep breathing steady!", "Progress alert: Fifty percent of active interval completed.")
            }
            else -> text // Default: "Sarah (High Energy)"
        }

        _spokenText.value = customizedText
        if (isTtsReady.value) {
            tts?.speak(customizedText, TextToSpeech.QUEUE_FLUSH, null, "C25K_COACH")
        }
    }

    private fun getPacingAudioMessage(phaseType: PhaseType, elapsedMinutes: Int, style: String): String {
        return when (phaseType) {
            PhaseType.RUN -> {
                when (style) {
                    "Coach John (Calm & Steady)" -> {
                        when (elapsedMinutes % 4) {
                            1 -> "Pacing tip: Check your posture. Keep your torso tall and shoulders relaxed."
                            2 -> "Pacing tip: Focus on deep nasal breathing. Keep a light, steady stride."
                            3 -> "Pacing advice: Step light on your feet. Keep a comfortable, sustainable pace."
                            else -> "Form check: Keep your head up. Focus on the path ahead."
                        }
                    }
                    "Robot Coach (Precise)" -> {
                        when (elapsedMinutes % 4) {
                            1 -> "Telemetry check: Cadence constant. Target pace achieved."
                            2 -> "Telemetry check: Oxygen consumption normal. Maintain one hundred sixty steps per minute."
                            3 -> "Telemetry check: Ground impact symmetric. Maintain running form."
                            else -> "Telemetry check: Energy expenditure optimal. Continue present velocity."
                        }
                    }
                    else -> { // Sarah (High Energy)
                        when (elapsedMinutes % 4) {
                            1 -> "Pacing check: You are flying! Keep a high, steady cadence. You've got this!"
                            2 -> "Pacing check: Focus on your breathing! Deep breaths in, deep breaths out. Stay strong!"
                            3 -> "Form check: Pump those arms, keep your chin up and smile! You are crushing it!"
                            else -> "Pacing check: Incredible pace! Let's keep this momentum going right to the transition!"
                        }
                    }
                }
            }
            PhaseType.WALK -> {
                when (style) {
                    "Coach John (Calm & Steady)" -> {
                        "Pacing tip: Active recovery. Relax your arms and focus on deep, steady breaths."
                    }
                    "Robot Coach (Precise)" -> {
                        "Telemetry check: Walking phase. Heart rate recovery in progress."
                    }
                    else -> { // Sarah (High Energy)
                        "Pacing check: Active recovery walk! Shake out those legs and enjoy the breather!"
                    }
                }
            }
            else -> {
                "Pacing check: Keep moving at a relaxed, gentle pace."
            }
        }
    }

    fun getCustomizedSession(week: Int, sessionIndex: Int, level: String): JoggingSession? {
        val session = JoggingPlan.weeksList[week]?.find { it.sessionIndex == sessionIndex } ?: return null
        val customizedPhases = JoggingPlan.getCustomizedPhases(session.phases, level)
        return session.copy(phases = customizedPhases)
    }

    fun startWorkout(session: JoggingSession) {
        _activeSession.value = session
        _currentPhaseIndex.value = 0
        _isPaused.value = false
        _isWorkoutActive.value = true

        val firstPhase = session.phases.firstOrNull()
        if (firstPhase != null) {
            _phaseSecondsRemaining.value = firstPhase.durationSeconds
            _phaseSecondsElapsed.value = 0
            speak(firstPhase.message)
        }

        workoutRunSeconds.value = 0
        workoutDurationSeconds.value = 0
        workoutDistanceKm.value = 0.0
        workoutCalories.value = 0

        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                if (!_isPaused.value && _isWorkoutActive.value) {
                    val session = _activeSession.value ?: break
                    val phases = session.phases
                    val currentPhaseIdx = _currentPhaseIndex.value

                    if (currentPhaseIdx < phases.size) {
                        val currentPhase = phases[currentPhaseIdx]
                        val settings = userSettings.value
                        
                        // Increment total duration
                        workoutDurationSeconds.value += 1
                        
                        // Increment real-time distance and calories based on active phase type
                        if (currentPhase.type == PhaseType.RUN) {
                            workoutRunSeconds.value += 1
                            workoutDistanceKm.value += 0.0025 // Approx 9 km/h (2.5 meters/sec)
                            workoutCalories.value += 1 // Approx 10 kcal/minute (0.16 / sec)
                        } else {
                            workoutDistanceKm.value += 0.0015 // Approx 5.4 km/h (1.5 meters/sec)
                            workoutCalories.value += 1 // Approx 5 kcal/minute
                        }

                        // Round distance to 3 decimals
                        workoutDistanceKm.value = Math.round(workoutDistanceKm.value * 1000.0) / 1000.0

                        _phaseSecondsRemaining.value -= 1
                        _phaseSecondsElapsed.value += 1

                        // Periodically trigger automated audio guidance pacing & interval tips
                        val freq = settings.coachingFrequencySeconds
                        if (settings.voicePacingEnabled && _phaseSecondsElapsed.value > 0 && _phaseSecondsElapsed.value % freq == 0) {
                            // Don't interrupt if we are right on the verge of transitioning (within 10 seconds)
                            if (_phaseSecondsRemaining.value > 10) {
                                val pacingMsg = getPacingAudioMessage(
                                    phaseType = currentPhase.type,
                                    elapsedMinutes = _phaseSecondsElapsed.value / freq,
                                    style = settings.voiceCoachStyle
                                )
                                speak(pacingMsg)
                            }
                        }

                        // Give halfway cue for longer runs (e.g., > 3 mins)
                        if (currentPhase.durationSeconds > 180 && 
                            _phaseSecondsRemaining.value == currentPhase.durationSeconds / 2) {
                            speak("You are halfway through this phase. Keep breathing steady!")
                        }

                        // Phase complete transition
                        if (_phaseSecondsRemaining.value <= 0) {
                            val nextIndex = currentPhaseIdx + 1
                            if (nextIndex < phases.size) {
                                _currentPhaseIndex.value = nextIndex
                                val nextPhase = phases[nextIndex]
                                _phaseSecondsRemaining.value = nextPhase.durationSeconds
                                _phaseSecondsElapsed.value = 0
                                speak(nextPhase.message)
                            } else {
                                // Workout complete
                                finishWorkout(completedSuccessfully = true)
                                break
                            }
                        }
                    } else {
                        finishWorkout(completedSuccessfully = true)
                        break
                    }
                }
            }
        }
    }

    fun pauseWorkout() {
        _isPaused.value = true
        speak("Workout paused.")
    }

    fun resumeWorkout() {
        _isPaused.value = false
        val currentPhaseIdx = _currentPhaseIndex.value
        val session = _activeSession.value
        if (session != null && currentPhaseIdx < session.phases.size) {
            val phase = session.phases[currentPhaseIdx]
            speak("Resuming ${phase.type.name} phase.")
        } else {
            speak("Resuming workout.")
        }
    }

    fun skipPhase() {
        val session = _activeSession.value ?: return
        val currentIdx = _currentPhaseIndex.value
        val nextIndex = currentIdx + 1
        if (nextIndex < session.phases.size) {
            _currentPhaseIndex.value = nextIndex
            val nextPhase = session.phases[nextIndex]
            _phaseSecondsRemaining.value = nextPhase.durationSeconds
            _phaseSecondsElapsed.value = 0
            speak(nextPhase.message)
        } else {
            finishWorkout(completedSuccessfully = true)
        }
    }

    fun finishWorkout(completedSuccessfully: Boolean) {
        timerJob?.cancel()
        _isWorkoutActive.value = false

        if (completedSuccessfully) {
            val session = _activeSession.value
            if (session != null) {
                speak("Congratulations! You completed today's session. Outstanding work!")
                
                // Persist completed workout to Room
                viewModelScope.launch {
                    val completed = CompletedWorkout(
                        week = session.week,
                        sessionIndex = session.sessionIndex,
                        durationSeconds = workoutDurationSeconds.value,
                        runSeconds = workoutRunSeconds.value,
                        distanceKm = workoutDistanceKm.value,
                        calories = workoutCalories.value,
                        difficultyRating = 3 // Default, can be rated in UI
                    )
                    repository.saveCompletedWorkout(completed)
                    
                    // Automatically increment settings currentWeek if they finished the 3rd session
                    val currentSettings = userSettings.value
                    if (session.sessionIndex == 3 && currentSettings.currentWeek == session.week && session.week < 8) {
                        repository.saveUserSettings(currentSettings.copy(currentWeek = session.week + 1))
                    }
                }
            }
        } else {
            speak("Workout stopped.")
        }
        _activeSession.value = null
    }

    // Settings adjustments
    fun updateSchedulePreferences(level: String, days: List<String>, time: String) {
        viewModelScope.launch {
            val current = userSettings.value
            val updated = current.copy(
                joggingLevel = level,
                preferredDays = days.joinToString(", "),
                preferredTime = time
            )
            repository.saveUserSettings(updated)
            speak("Schedule updated. Training plan is now personalized for $level on $days.")
        }
    }

    fun updateVoicePreferences(
        enabled: Boolean,
        pacingEnabled: Boolean,
        style: String,
        frequencySeconds: Int
    ) {
        viewModelScope.launch {
            val current = userSettings.value
            val updated = current.copy(
                voiceCoachEnabled = enabled,
                voicePacingEnabled = pacingEnabled,
                voiceCoachStyle = style,
                coachingFrequencySeconds = frequencySeconds
            )
            repository.saveUserSettings(updated)
            
            // Wait briefly for Flow to update, then confirm vocally if voice is enabled
            delay(100)
            if (enabled) {
                speak("Voice coach configured successfully using $style. Your pacing updates are ready.")
            } else {
                _spokenText.value = "Voice coach disabled."
            }
        }
    }

    fun setWeek(week: Int) {
        viewModelScope.launch {
            val current = userSettings.value
            repository.saveUserSettings(current.copy(currentWeek = week))
        }
    }

    fun deleteWorkout(id: Int) {
        viewModelScope.launch {
            repository.deleteCompletedWorkout(id)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearCompletedWorkouts()
            speak("Workout history cleared.")
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        tts?.stop()
        tts?.shutdown()
    }
}
