# Stride 5K 🏃‍♂️⚡

Stride 5K is a premium, feature-rich Couch to 5K (C25K) jogging coach application designed to guide beginners safely and beautifully from the couch to running a full 5K. Built using Kotlin, Jetpack Compose, and Material Design 3, it offers local progress tracking, flexible scheduling, plan personalization, and an intelligent **Automated Audio Guidance Engine** via Android's Text-to-Speech (TTS) framework.

---

## ✨ Key Features

### 🎙️ Automated Audio Guidance & Pacing Engine
An interactive, context-aware Voice Coach provides automated coaching, interval pacing tips, and countdowns:
*   **Contextual Cueing**: Automatically speaks instructions at transitions (e.g., transitioning from walk to run) and provides a halfway cue for longer running intervals.
*   **Diverse Coach Personas**: Choose your coaching partner:
    *   **Sarah (High Energy)**: Encouraging, high-octane motivational prompts ("You're crushing it! Stay strong!").
    *   **Coach John (Calm & Steady)**: Quiet, steady, mindfulness-focused prompts ("Relax your shoulders. Deep nasal breaths.").
    *   **Robot Coach (Precise)**: Telemetry-themed, highly systematic updates ("Cadence check. Ground impact symmetric. Maintain running form.").
*   **Custom Alert Frequencies**: Configure pacing tips and posture reminders to speak every 30s, 60s, or 90s.
*   **Transition Protection**: Intelligent timer logic ensures pacing tips do not interrupt imminent walk/run transitions (safeguarded within the final 10 seconds of any phase).

### 📅 Highly Personalized 8-Week C25K Plan
*   **Intensity Scaling**: Tailor the program to your fitness level:
    *   **Absolute Beginner**: Shorter intervals (~22 mins total per run).
    *   **Active Beginner**: Standard intervals (~28 mins total per run).
    *   **Re-starter**: Enhanced endurance-building intervals (~32 mins total per run).
*   **Preferred Scheduling**: Select your preferred 3 running days (e.g., Mon, Wed, Fri) and target running times.
*   **Progress Dashboard**: Displays your overall program progression, custom week selector, and list of daily sessions.

### 📊 Real-Time Interactive Coaching HUD & Progress Stats
*   **State-Driven Audio Meter**: High-contrast circular training gauge that updates dynamically matching your current phase (Warmup/Cooldown = Gold, Walk = Primary Blue, Run = Coral).
*   **Instant Play/Pause/Stop/Skip Controls**: Clean media control bar supporting easy interactions while jogging.
*   **Live Metrics Grid**: Displays real-time estimates for total active duration, distance traveled, and active calories burned.
*   **Run History & Stats**: Track total runs logged, cumulative distance (km), active time (mins), and custom weekly activity trend bar charts.

### 💾 Persistent Offline Architecture (Room DB)
*   **Database Integration**: Secure, local data persistence using Android Room Database.
*   **Auto-Saves**: Seamlessly saves personal schedule settings, preferred voice options, and workout histories so your data remains intact offline.

---

## 🛠️ Technology Stack & Architecture

*   **Jetpack Compose**: Modern declarative UI framework utilizing rich Material Design 3 tokens.
*   **State & Concurrency**: Structured using Kotlin Coroutines, StateFlow, and `ViewModel` for reactive state flows.
*   **Data Engine**: Room Database + SQLite for persistent data models.
*   **Audio Engine**: Android native `TextToSpeech` API for real-time local voice synthesis.

---

## 🚀 How to Run the Project

1.  **Direct APK Installation**:
    *   A pre-compiled, real installable debug APK is fully built and ready for download!
    *   **Location**: `APK_DOWNLOAD/app-debug.apk` (also mirrored at `.build-outputs/app-debug.apk`).
    *   Simply transfer `app-debug.apk` to your Android device, enable "Install from Unknown Sources", and launch **Stride 5K** immediately on your phone!

2.  **Open in Google AI Studio**:
    *   View and interact with your app using the built-in streaming emulator in the browser.

3.  **To Build & Run locally via Gradle**:
    *   Ensure Android SDK 34+ is installed.
    *   Build the debug application target manually:
        ```bash
        gradle assembleDebug
        ```
    *   Run local JVM tests to verify application integrity:
        ```bash
        gradle :app:testDebugUnitTest
        ```

---

## 🎨 Design Theme & Colors

Stride 5K adheres to a high-contrast, modern visual style tailored for outdoor visibility:
*   **Primary Accent**: Bold cobalt blue for major navigation targets and active walk indicators.
*   **Run Color**: Coral red representing high intensity running phases.
*   **Tonal Surfaces**: Light slate backgrounds coupled with rounded Material 3 cards for maximum text readability in sunny conditions.
