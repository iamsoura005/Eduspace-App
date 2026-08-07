# Eduspace

A role-based academic ERP for Android — built with Jetpack Compose and Material 3. Manages authentication, attendance, timetables, courses, library, exams, and finance behind adaptive, security-first navigation.

## Features

- **Biometric security** — fingerprint / face sign-in via AndroidX Biometric, plus PIN-based app lock with session timeout
- **Role-based access** — Student, COE (Controller of Exams), and IT Admin get tailored dashboards and permissions
- **Attendance management** — students lock attendance in‑class; COE reviews, corrects, and deletes records
- **Courses & timetables** — browse the roster and view class/exam schedules
- **Portals** — Library catalog, Exam hub, and a Finance portal where payments can be gated behind biometrics
- **Admin control** — user lifecycle, role/status management, password resets, and a full audit log
- **Responsive UI** — adaptive bottom navigation on phones, navigation rail on tablets and large screens
- **Immersive dark theming** — Material 3 permissive color tokens applied across the app

## Tech Stack

| Layer    | Choice |
| -------- | ------ |
| Language | Kotlin 2.2 |
| UI       | Jetpack Compose + Material 3 |
| Architecture | Single-activity, in-memory repository + UI state flows |
| Persistence | DataStore (security preferences) |
| Security | SHA-256 (salted) passwords, AndroidX Biometric |
| Tests    | JUnit, Robolectric, Roborazzi screenshot tests, Compose UI tests |

## Getting Started

Requires Android Studio (recommended) — open the project root and let the IDE sync Gradle, then run the `app` configuration on a device or emulator. Requires a JDK 17+ toolchain.

### Demo accounts

Seeded in-memory accounts for exploring every role:

| Role | Email | Password |
| ---- | ----- | -------- |
| Student | `alex.student@eduspace.edu` | `student123` |
| COE (Controller of Exams) | `clara.coe@eduspace.edu` | `coe123` |
| IT Admin | `admin@eduspace.edu` | `admin123` |

### Release signing

Release builds read `KEYSTORE_PATH`, `STORE_PASSWORD`, and `KEY_PASSWORD` from the environment (defaults to `my-upload-key.jks` in the project root).

## Testing

Unit, Robolectric, and Roborazzi screenshot tests cover the Compose UI — run them from Android Studio or the Gradle test task:

```bash
./gradlew testDebugUnitTest
```

(If the Gradle Wrapper is absent from this checkout, generate it in the IDE or run the equivalent `gradle` task with your local install.)

## Project Structure

```
app/src/main/java/com/example
├── auth/          Biometric manager, password hashing, user models
├── data/          In-memory repository, DataStore security preferences
├── ui/components  Adaptive navigation, timetables
├── ui/screens     Dashboards and portals per role
├── ui/theme       Material 3 colour scheme and typography
└── utils/         Responsive sizing, window size classes
```