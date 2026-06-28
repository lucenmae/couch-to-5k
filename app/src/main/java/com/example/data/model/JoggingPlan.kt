package com.example.data.model

enum class PhaseType {
    WARMUP, RUN, WALK, COOLDOWN
}

data class WorkoutPhase(
    val type: PhaseType,
    val durationSeconds: Int,
    val message: String
)

data class JoggingSession(
    val week: Int,
    val sessionIndex: Int,
    val title: String,
    val description: String,
    val phases: List<WorkoutPhase>
)

object JoggingPlan {
    // Generates the customized list of phases based on user level
    fun getCustomizedPhases(basePhases: List<WorkoutPhase>, level: String): List<WorkoutPhase> {
        return basePhases.map { phase ->
            when (phase.type) {
                PhaseType.RUN -> {
                    val scale = when (level) {
                        "Absolute Beginner" -> 0.8  // 20% less running
                        "Re-starter" -> 1.15       // 15% more running
                        else -> 1.0                // standard Active Beginner
                    }
                    val rawDuration = (phase.durationSeconds * scale).toInt()
                    // Round to nearest 5 or 10 seconds for clean numbers
                    val rounded = ((rawDuration + 4) / 5) * 5
                    val message = when (level) {
                        "Absolute Beginner" -> "Begin running for ${rounded / 60} minutes and ${rounded % 60} seconds at a relaxed pace."
                        "Re-starter" -> "Let's push! Run for ${rounded / 60} minutes and ${rounded % 60} seconds."
                        else -> phase.message
                    }
                    phase.copy(durationSeconds = rounded, message = message)
                }
                PhaseType.WALK -> {
                    val scale = when (level) {
                        "Absolute Beginner" -> 1.2  // 20% more recovery walk
                        "Re-starter" -> 0.85       // 15% less recovery walk
                        else -> 1.0
                    }
                    val rawDuration = (phase.durationSeconds * scale).toInt()
                    val rounded = ((rawDuration + 4) / 5) * 5
                    val message = "Walk and catch your breath for ${rounded / 60} minutes and ${rounded % 60} seconds."
                    phase.copy(durationSeconds = rounded, message = message)
                }
                else -> phase // Keep warmup and cooldown unchanged (usually 5 mins each)
            }
        }
    }

    val weeksList: Map<Int, List<JoggingSession>> = mapOf(
        1 to listOf(
            JoggingSession(
                week = 1, sessionIndex = 1,
                title = "W1 Session 1: Gentle Introduction",
                description = "Alternating 6 sets of 1 min run and 1.5 min walk.",
                phases = generateIntervals(1 * 60, 90, 6)
            ),
            JoggingSession(
                week = 1, sessionIndex = 2,
                title = "W1 Session 2: Rhythm Finding",
                description = "Alternating 6 sets of 1 min run and 1.5 min walk.",
                phases = generateIntervals(1 * 60, 90, 6)
            ),
            JoggingSession(
                week = 1, sessionIndex = 3,
                title = "W1 Session 3: Graduation Run",
                description = "Alternating 6 sets of 1 min run and 1.5 min walk.",
                phases = generateIntervals(1 * 60, 90, 6)
            )
        ),
        2 to listOf(
            JoggingSession(
                week = 2, sessionIndex = 1,
                title = "W2 Session 1: Stepping It Up",
                description = "Alternating 6 sets of 1.5 min run and 2 min walk.",
                phases = generateIntervals(90, 2 * 60, 6)
            ),
            JoggingSession(
                week = 2, sessionIndex = 2,
                title = "W2 Session 2: Steady Cadence",
                description = "Alternating 6 sets of 1.5 min run and 2 min walk.",
                phases = generateIntervals(90, 2 * 60, 6)
            ),
            JoggingSession(
                week = 2, sessionIndex = 3,
                title = "W2 Session 3: Week 2 Summit",
                description = "Alternating 6 sets of 1.5 min run and 2 min walk.",
                phases = generateIntervals(90, 2 * 60, 6)
            )
        ),
        3 to listOf(
            JoggingSession(
                week = 3, sessionIndex = 1,
                title = "W3 Session 1: Deep Breathing",
                description = "2 sets of: 1.5m run, 1.5m walk, 3m run, 3m walk.",
                phases = listOf(
                    WorkoutPhase(PhaseType.WARMUP, 300, "Start with a 5-minute warm-up walk to prepare your body."),
                    WorkoutPhase(PhaseType.RUN, 90, "Run for 1 minute and 30 seconds."),
                    WorkoutPhase(PhaseType.WALK, 90, "Walk and recover for 1 minute and 30 seconds."),
                    WorkoutPhase(PhaseType.RUN, 180, "Run for 3 minutes. Focus on steady pacing."),
                    WorkoutPhase(PhaseType.WALK, 180, "Walk and recover for 3 minutes."),
                    WorkoutPhase(PhaseType.RUN, 90, "Run for 1 minute and 30 seconds."),
                    WorkoutPhase(PhaseType.WALK, 90, "Walk and recover for 1 minute and 30 seconds."),
                    WorkoutPhase(PhaseType.RUN, 180, "Final run! 3 minutes of jogging."),
                    WorkoutPhase(PhaseType.COOLDOWN, 300, "Great job. Cool down with a 5-minute walk to lower your heart rate.")
                )
            ),
            JoggingSession(
                week = 3, sessionIndex = 2,
                title = "W3 Session 2: Consistency",
                description = "2 sets of: 1.5m run, 1.5m walk, 3m run, 3m walk.",
                phases = listOf(
                    WorkoutPhase(PhaseType.WARMUP, 300, "Start with a 5-minute warm-up walk to prepare your body."),
                    WorkoutPhase(PhaseType.RUN, 90, "Run for 1 minute and 30 seconds."),
                    WorkoutPhase(PhaseType.WALK, 90, "Walk and recover for 1 minute and 30 seconds."),
                    WorkoutPhase(PhaseType.RUN, 180, "Run for 3 minutes. Focus on steady pacing."),
                    WorkoutPhase(PhaseType.WALK, 180, "Walk and recover for 3 minutes."),
                    WorkoutPhase(PhaseType.RUN, 90, "Run for 1 minute and 30 seconds."),
                    WorkoutPhase(PhaseType.WALK, 90, "Walk and recover for 1 minute and 30 seconds."),
                    WorkoutPhase(PhaseType.RUN, 180, "Final run! 3 minutes of jogging."),
                    WorkoutPhase(PhaseType.COOLDOWN, 300, "Great job. Cool down with a 5-minute walk to lower your heart rate.")
                )
            ),
            JoggingSession(
                week = 3, sessionIndex = 3,
                title = "W3 Session 3: Strength Builder",
                description = "2 sets of: 1.5m run, 1.5m walk, 3m run, 3m walk.",
                phases = listOf(
                    WorkoutPhase(PhaseType.WARMUP, 300, "Start with a 5-minute warm-up walk to prepare your body."),
                    WorkoutPhase(PhaseType.RUN, 90, "Run for 1 minute and 30 seconds."),
                    WorkoutPhase(PhaseType.WALK, 90, "Walk and recover for 1 minute and 30 seconds."),
                    WorkoutPhase(PhaseType.RUN, 180, "Run for 3 minutes. Focus on steady pacing."),
                    WorkoutPhase(PhaseType.WALK, 180, "Walk and recover for 3 minutes."),
                    WorkoutPhase(PhaseType.RUN, 90, "Run for 1 minute and 30 seconds."),
                    WorkoutPhase(PhaseType.WALK, 90, "Walk and recover for 1 minute and 30 seconds."),
                    WorkoutPhase(PhaseType.RUN, 180, "Final run! 3 minutes of jogging."),
                    WorkoutPhase(PhaseType.COOLDOWN, 300, "Great job. Cool down with a 5-minute walk to lower your heart rate.")
                )
            )
        ),
        4 to listOf(
            JoggingSession(
                week = 4, sessionIndex = 1,
                title = "W4 Session 1: Crossing Thresholds",
                description = "3m run, 1.5m walk, 5m run, 2.5m walk, 3m run, 1.5m walk, 5m run.",
                phases = listOf(
                    WorkoutPhase(PhaseType.WARMUP, 300, "Start with a 5-minute warm-up walk."),
                    WorkoutPhase(PhaseType.RUN, 180, "Run for 3 minutes. Relax your shoulders."),
                    WorkoutPhase(PhaseType.WALK, 90, "Walk for 1 minute and 30 seconds."),
                    WorkoutPhase(PhaseType.RUN, 300, "Now run for 5 minutes. Find your breathing rhythm."),
                    WorkoutPhase(PhaseType.WALK, 150, "Walk and recover for 2 minutes and 30 seconds."),
                    WorkoutPhase(PhaseType.RUN, 180, "Run for 3 minutes. Stay strong."),
                    WorkoutPhase(PhaseType.WALK, 90, "Walk for 1 minute and 30 seconds."),
                    WorkoutPhase(PhaseType.RUN, 300, "Final push! Run for 5 minutes."),
                    WorkoutPhase(PhaseType.COOLDOWN, 300, "Brilliant effort! Cool down with a 5-minute walk.")
                )
            ),
            JoggingSession(
                week = 4, sessionIndex = 2,
                title = "W4 Session 2: Endurance Base",
                description = "3m run, 1.5m walk, 5m run, 2.5m walk, 3m run, 1.5m walk, 5m run.",
                phases = listOf(
                    WorkoutPhase(PhaseType.WARMUP, 300, "Start with a 5-minute warm-up walk."),
                    WorkoutPhase(PhaseType.RUN, 180, "Run for 3 minutes. Relax your shoulders."),
                    WorkoutPhase(PhaseType.WALK, 90, "Walk for 1 minute and 30 seconds."),
                    WorkoutPhase(PhaseType.RUN, 300, "Now run for 5 minutes. Find your breathing rhythm."),
                    WorkoutPhase(PhaseType.WALK, 150, "Walk and recover for 2 minutes and 30 seconds."),
                    WorkoutPhase(PhaseType.RUN, 180, "Run for 3 minutes. Stay strong."),
                    WorkoutPhase(PhaseType.WALK, 90, "Walk for 1 minute and 30 seconds."),
                    WorkoutPhase(PhaseType.RUN, 300, "Final push! Run for 5 minutes."),
                    WorkoutPhase(PhaseType.COOLDOWN, 300, "Brilliant effort! Cool down with a 5-minute walk.")
                )
            ),
            JoggingSession(
                week = 4, sessionIndex = 3,
                title = "W4 Session 3: Mind Over Matter",
                description = "3m run, 1.5m walk, 5m run, 2.5m walk, 3m run, 1.5m walk, 5m run.",
                phases = listOf(
                    WorkoutPhase(PhaseType.WARMUP, 300, "Start with a 5-minute warm-up walk."),
                    WorkoutPhase(PhaseType.RUN, 180, "Run for 3 minutes. Relax your shoulders."),
                    WorkoutPhase(PhaseType.WALK, 90, "Walk for 1 minute and 30 seconds."),
                    WorkoutPhase(PhaseType.RUN, 300, "Now run for 5 minutes. Find your breathing rhythm."),
                    WorkoutPhase(PhaseType.WALK, 150, "Walk and recover for 2 minutes and 30 seconds."),
                    WorkoutPhase(PhaseType.RUN, 180, "Run for 3 minutes. Stay strong."),
                    WorkoutPhase(PhaseType.WALK, 90, "Walk for 1 minute and 30 seconds."),
                    WorkoutPhase(PhaseType.RUN, 300, "Final push! Run for 5 minutes."),
                    WorkoutPhase(PhaseType.COOLDOWN, 300, "Brilliant effort! Cool down with a 5-minute walk.")
                )
            )
        ),
        5 to listOf(
            JoggingSession(
                week = 5, sessionIndex = 1,
                title = "W5 Session 1: The Triple Five",
                description = "3 sets of: 5 min run and 3 min walk.",
                phases = listOf(
                    WorkoutPhase(PhaseType.WARMUP, 300, "Let's warm up with a 5-minute walk."),
                    WorkoutPhase(PhaseType.RUN, 300, "Run for 5 minutes."),
                    WorkoutPhase(PhaseType.WALK, 180, "Walk and recover for 3 minutes."),
                    WorkoutPhase(PhaseType.RUN, 300, "Run for 5 minutes."),
                    WorkoutPhase(PhaseType.WALK, 180, "Walk and recover for 3 minutes."),
                    WorkoutPhase(PhaseType.RUN, 300, "Final 5-minute run! Maintain focus."),
                    WorkoutPhase(PhaseType.COOLDOWN, 300, "Superb! Cool down with a 5-minute walk.")
                )
            ),
            JoggingSession(
                week = 5, sessionIndex = 2,
                title = "W5 Session 2: Double Eight",
                description = "2 sets of: 8 min run and 5 min walk.",
                phases = listOf(
                    WorkoutPhase(PhaseType.WARMUP, 300, "Let's warm up with a 5-minute walk."),
                    WorkoutPhase(PhaseType.RUN, 480, "Run for 8 minutes. Pace yourself steadily."),
                    WorkoutPhase(PhaseType.WALK, 300, "Walk and recover for 5 minutes. Enjoy the breeze."),
                    WorkoutPhase(PhaseType.RUN, 480, "Second run! 8 minutes of jogging. You can do this!"),
                    WorkoutPhase(PhaseType.COOLDOWN, 300, "Phenomenal! Cool down with a 5-minute walk.")
                )
            ),
            JoggingSession(
                week = 5, sessionIndex = 3,
                title = "W5 Session 3: The 20-Min Milestone",
                description = "5 min walk, then 20 minutes continuous running.",
                phases = listOf(
                    WorkoutPhase(PhaseType.WARMUP, 300, "Let's warm up with a 5-minute walk. Mentally prepare for today's milestone."),
                    WorkoutPhase(PhaseType.RUN, 1200, "Begin your 20-minute continuous run. Take it slow, keep a steady breath."),
                    WorkoutPhase(PhaseType.COOLDOWN, 300, "You did it! 20 minutes of non-stop running. Cool down with a 5-minute walk.")
                )
            )
        ),
        6 to listOf(
            JoggingSession(
                week = 6, sessionIndex = 1,
                title = "W6 Session 1: Re-establishing Base",
                description = "5m run, 3m walk, 8m run, 3m walk, 5m run.",
                phases = listOf(
                    WorkoutPhase(PhaseType.WARMUP, 300, "Start with a 5-minute warm-up walk."),
                    WorkoutPhase(PhaseType.RUN, 300, "Run for 5 minutes."),
                    WorkoutPhase(PhaseType.WALK, 180, "Walk for 3 minutes."),
                    WorkoutPhase(PhaseType.RUN, 480, "Now run for 8 minutes. Keep your posture tall."),
                    WorkoutPhase(PhaseType.WALK, 180, "Walk and catch your breath for 3 minutes."),
                    WorkoutPhase(PhaseType.RUN, 300, "Final run! 5 minutes of jogging."),
                    WorkoutPhase(PhaseType.COOLDOWN, 300, "Excellent effort. Cool down with a 5-minute walk.")
                )
            ),
            JoggingSession(
                week = 6, sessionIndex = 2,
                title = "W6 Session 2: Double Ten",
                description = "2 sets of: 10 min run and 3 min walk.",
                phases = listOf(
                    WorkoutPhase(PhaseType.WARMUP, 300, "Start with a 5-minute warm-up walk."),
                    WorkoutPhase(PhaseType.RUN, 600, "Run for 10 minutes. Relax your arms."),
                    WorkoutPhase(PhaseType.WALK, 180, "Walk for 3 minutes. Rest active."),
                    WorkoutPhase(PhaseType.RUN, 600, "Run for 10 minutes. Find your rhythm."),
                    WorkoutPhase(PhaseType.COOLDOWN, 300, "Sensational job! Cool down with a 5-minute walk.")
                )
            ),
            JoggingSession(
                week = 6, sessionIndex = 3,
                title = "W6 Session 3: The 25-Min Milestone",
                description = "5 min walk, then 25 minutes continuous running.",
                phases = listOf(
                    WorkoutPhase(PhaseType.WARMUP, 300, "Start with a 5-minute warm-up walk."),
                    WorkoutPhase(PhaseType.RUN, 1500, "Begin your 25-minute continuous run. Relax into the pace."),
                    WorkoutPhase(PhaseType.COOLDOWN, 300, "Unbelievable! You ran for 25 minutes straight. Cool down with a 5-minute walk.")
                )
            )
        ),
        7 to listOf(
            JoggingSession(
                week = 7, sessionIndex = 1,
                title = "W7 Session 1: Solidifying Pace",
                description = "5 min walk, then 25 minutes continuous running.",
                phases = listOf(
                    WorkoutPhase(PhaseType.WARMUP, 300, "Warm up with a 5-minute walk."),
                    WorkoutPhase(PhaseType.RUN, 1500, "Run for 25 minutes. Consistency is key."),
                    WorkoutPhase(PhaseType.COOLDOWN, 300, "Superb run! Cool down with a 5-minute walk.")
                )
            ),
            JoggingSession(
                week = 7, sessionIndex = 2,
                title = "W7 Session 2: Focus & Form",
                description = "5 min walk, then 25 minutes continuous running.",
                phases = listOf(
                    WorkoutPhase(PhaseType.WARMUP, 300, "Warm up with a 5-minute walk."),
                    WorkoutPhase(PhaseType.RUN, 1500, "Run for 25 minutes. Keep your strides light."),
                    WorkoutPhase(PhaseType.COOLDOWN, 300, "Terrific work. Cool down with a 5-minute walk.")
                )
            ),
            JoggingSession(
                week = 7, sessionIndex = 3,
                title = "W7 Session 3: Peak Week 7",
                description = "5 min walk, then 25 minutes continuous running.",
                phases = listOf(
                    WorkoutPhase(PhaseType.WARMUP, 300, "Warm up with a 5-minute walk."),
                    WorkoutPhase(PhaseType.RUN, 1500, "Run for 25 minutes. You are nearly at 5k."),
                    WorkoutPhase(PhaseType.COOLDOWN, 300, "Amazing week 7 completed! Cool down with a 5-minute walk.")
                )
            )
        ),
        8 to listOf(
            JoggingSession(
                week = 8, sessionIndex = 1,
                title = "W8 Session 1: 5K Horizon",
                description = "5 min walk, then 30 minutes continuous running.",
                phases = listOf(
                    WorkoutPhase(PhaseType.WARMUP, 300, "Warm up with a 5-minute walk. The final week begins."),
                    WorkoutPhase(PhaseType.RUN, 1800, "Begin your 30-minute run. Breathe, stay calm, and enjoy."),
                    WorkoutPhase(PhaseType.COOLDOWN, 300, "Fabulous! You're 30 minutes stronger. Cool down with a 5-minute walk.")
                )
            ),
            JoggingSession(
                week = 8, sessionIndex = 2,
                title = "W8 Session 2: Pure Determination",
                description = "5 min walk, then 30 minutes continuous running.",
                phases = listOf(
                    WorkoutPhase(PhaseType.WARMUP, 300, "Warm up with a 5-minute walk."),
                    WorkoutPhase(PhaseType.RUN, 1800, "Run for 30 minutes. You have trained hard for this."),
                    WorkoutPhase(PhaseType.COOLDOWN, 300, "Incredible! One session left until graduation. Cool down with a 5-minute walk.")
                )
            ),
            JoggingSession(
                week = 8, sessionIndex = 3,
                title = "W8 Session 3: Graduation Run!",
                description = "5 min walk, then 30 minutes continuous running to graduate!",
                phases = listOf(
                    WorkoutPhase(PhaseType.WARMUP, 300, "Warm up with a 5-minute walk. This is your Graduation 5K Run!"),
                    WorkoutPhase(PhaseType.RUN, 1800, "Go! Run for 30 minutes. Celebrate how far you've come from 0!"),
                    WorkoutPhase(PhaseType.COOLDOWN, 300, "CONGRATULATIONS! You have graduated 0 to 5K! Cool down with a proud 5-minute walk.")
                )
            )
        )
    )

    private fun generateIntervals(runSec: Int, walkSec: Int, repeats: Int): List<WorkoutPhase> {
        val phases = mutableListOf<WorkoutPhase>()
        phases.add(WorkoutPhase(PhaseType.WARMUP, 300, "Warm up with a brisk 5-minute walk."))
        for (i in 1..repeats) {
            phases.add(WorkoutPhase(PhaseType.RUN, runSec, "Run for ${runSec / 60} minutes ${if (runSec % 60 > 0) "and ${runSec % 60} seconds" else ""}. Keep a comfortable pace."))
            phases.add(WorkoutPhase(PhaseType.WALK, walkSec, "Walk and catch your breath for ${walkSec / 60} minutes ${if (walkSec % 60 > 0) "and ${walkSec % 60} seconds" else ""}."))
        }
        phases.add(WorkoutPhase(PhaseType.COOLDOWN, 300, "Outstanding! You're done. Cool down with a gentle 5-minute walk."))
        return phases
    }
}
