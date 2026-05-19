# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build
./gradlew assembleDebug
./gradlew assembleRelease

# Test
./gradlew test                    # Unit tests
./gradlew connectedAndroidTest    # Instrumented tests (requires device/emulator)

# Lint
./gradlew lint

# Single test class
./gradlew test --tests "com.example.oktodo.ExampleUnitTest"
```

On Windows, use `gradlew.bat` or prefix with `.\` in PowerShell.

## Architecture

Single-module Android app using **MVVM + Repository pattern** with Jetpack Compose UI (no XML layouts).

### Layer responsibilities

- **`data/local/entity/`** — Room `@Entity` classes (database schema)
- **`data/local/dao/`** — Room `@Dao` interfaces (SQL queries as Kotlin flows)
- **`data/local/mapper/`** — `Entity.toDomain()` / `Domain.toEntity()` extension functions
- **`data/repository/`** — Repositories expose `Flow<List<DomainModel>>` from DAOs; single source of truth
- **`ui/model/`** — Domain/UI model classes (used by ViewModels and Compose screens)
- **`ui/viewmodel/`** — `@HiltViewModel` classes; expose `StateFlow` with `WhileSubscribed(5000)` sharing; write via repository methods
- **`ui/screens/`** — Composable screens; inject VM via `hiltViewModel()`; call `collectAsState()` on flows
- **`ui/components/`** — Reusable composables shared across screens (see component inventory below)
- **`di/DatabaseModule.kt`** — Hilt `@Module` providing `OktodoDatabase` singleton and all DAOs
- **`navigation/AppNavigation.kt`** — Single `NavHost` with bottom nav bar (5 tabs); `group_detail/{groupId}` is the only route with arguments

### ViewModels

| ViewModel | Screens that use it |
|---|---|
| `TasksViewModel` | DashboardScreen |
| `CalendarViewModel` | CalendarScreen |
| `ProfileViewModel` | ProfileScreen, EditProfileScreen — `updateProfile(displayName, username, avatarEmoji)` |
| `FriendsViewModel` | FriendsScreen, GroupDetailScreen |
| `ThemeViewModel` | MainActivity (dark-mode toggle) |

`FocusScreen` has **no ViewModel** — timer state is managed entirely with `remember` / coroutines inside the composable.

### Repositories

| Repository | Entities covered |
|---|---|
| `TaskRepository` | `tasks` |
| `CalendarEventRepository` | `calendar_events` |
| `FriendsRepository` | `friends`, `groups`, `shared_events` |
| `ShopItemRepository` | `shop_items` |

### Component inventory (`ui/components/`)

**Bottom sheets / dialogs**
- `CreateTaskBottomSheet` — new task form with date/time pickers
- `AddEventBottomSheet` — calendar event form with `DatePickerDialog` + `TimePickerDialog`
- `AddEventDialog` — compact event dialog (also uses Material3 pickers)
- `CreateSharedEventBottomSheet` — shared event form for groups
- `CreateGroupBottomSheet` — group creation form
- `DurationPickerDialog` — scroll-wheel hour/minute picker used by FocusScreen timer

**Calendar views**
- `MonthlyCalendar`, `WeekCalendar`, `DayCalendar`, `YearCalendar` — swappable calendar modes inside CalendarScreen

**List-item cards**
- `FriendCard`, `GroupCard`, `SharedEventCard`

**Friends screen chrome**
- `FriendsTabSwitcher`, `FriendsTopBar`

**Misc**
- `ColorPicker` — reusable color selection composable

### Data persistence

- **Room** (`oktodo.db`, version 1): 6 entities — `tasks`, `calendar_events`, `friends`, `groups`, `shared_events`, `shop_items`
- **DataStore** (`UserPreferencesDataStore`): user profile (`displayName`, `username`, `avatarEmoji`), `points`, `completedTasks`, `streakDays`, `lastActiveDate`, `weeklyGoal`, `weeklyCompleted`, `isDarkMode`
  - `updateStreak()` — called by `TasksViewModel` when a task is marked complete; increments `streakDays` if last active date was yesterday, resets to 1 otherwise; no-ops if already updated today

### Dependency injection

`OktodoApplication` is annotated `@HiltAndroidApp`. `MainActivity` is `@AndroidEntryPoint`. All ViewModels use `@HiltViewModel`. KSP (not kapt) handles annotation processing.

### Navigation routes

| Route | Screen |
|---|---|
| `dashboard` | DashboardScreen (start destination) |
| `calendario` | CalendarScreen |
| `focus` | FocusScreen |
| `friends` | FriendsScreen |
| `group_detail/{groupId}` | GroupDetailScreen |
| `profile` | ProfileScreen |
| `edit_profile` | EditProfileScreen |
| `octo_shop` | OctoShopScreen |

### Key tech versions

See `gradle/libs.versions.toml` for the full version catalog. Notable: Kotlin 2.0.21, Compose BOM 2024.09.00, Hilt 2.51.1, Room 2.6.1, Navigation Compose 2.7.7, compileSdk/targetSdk 35, minSdk 24, Java 17.

## UI patterns

- **Date/time inputs** — always use Material3 `DatePickerDialog` + `TimePickerDialog` (not free-text fields). All bottom sheets and dialogs that capture a date or time follow this pattern.
- **Completed tasks (Dashboard)** — completed tasks are hidden inside a collapsible `AnimatedVisibility` dropdown ("Completadas ▼/▲") so the active list stays clean. New task-list UX should follow this pattern.
- **Profile fields** — `ProfileUiState` (and DataStore) distinguish `displayName` (human-readable name shown in the UI) from `username` (handle/login). `EditProfileScreen` edits both independently.
- **Focus timer** — `FocusScreen` owns all timer state locally (`selectedDuration`, `timeLeft`, `isTimerRunning`). Duration is chosen via `DurationPickerDialog` (scroll-wheel, hours + minutes). Do not add a ViewModel for FocusScreen unless persistence across navigation is needed.

## Conventions

- All new screens must be added to both `AppNavigation.kt` (NavHost) and the bottom-nav list if they are top-level.
- New database tables require: a new `@Entity`, a `@Dao`, registration in `OktodoDatabase` entities list and `@Database` annotation, a domain model in `ui/model/`, mapper extensions, a repository, and a Hilt binding in `DatabaseModule`.
- ViewModels should never directly reference Room DAOs; always go through a Repository.
- UI state is `StateFlow`; side-effects (toasts, navigation) use `SharedFlow` or `Channel` in the ViewModel, not raw `LiveData`.
