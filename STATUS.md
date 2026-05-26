# STATUS — Oktodo

## Project Overview

Android task-management app built with **Kotlin + Jetpack Compose + Material3**.  
Follows **MVVM + Repository** pattern with **Room** for persistence and **DataStore** for preferences.

| Layer | Tech |
|---|---|
| Language | Kotlin 2.0.21 |
| UI | Jetpack Compose, Material3 |
| DI | Hilt 2.51.1 (KSP) |
| Database | Room 2.6.1 |
| Preferences | DataStore |
| Navigation | Navigation Compose 2.7.7 |
| Min SDK | 24 |
| Target / Compile SDK | 35 |

---

## Current Status

- **Active branch:** `develop` (ahead of `origin/develop`)
- **Last activity:** 26 May 2026 — DurationPickerDialog snap fix
- **Working tree:** clean

### Branches

| Branch | Details |
|---|---|
| `main` | Stable / release |
| `develop` | Active development |
| `gh-pages` | GitHub Pages (landing page) |

---

## What's Implemented

### Screens (10)

| Screen | Route | ViewModel |
|---|---|---|
| Dashboard | `dashboard` | TasksViewModel |
| Calendar | `calendario` | CalendarViewModel |
| Focus | `focus` | None (local state) |
| Friends | `friends` | FriendsViewModel |
| Group Detail | `group_detail/{groupId}` | FriendsViewModel |
| Profile | `profile` | ProfileViewModel |
| Settings | `settings` | ProfileViewModel |
| Octo Shop | `octo_shop` | ProfileViewModel |
| Notifications | `notifications` | NotificationViewModel |
| Edit Profile | `edit_profile` | ProfileViewModel |

### Data Layer

| Repository | Entities |
|---|---|
| TaskRepository | `tasks` |
| CalendarEventRepository | `calendar_events` |
| FriendsRepository | `friends`, `groups`, `shared_events`, `user_profiles`, `friend_requests`, `group_invitations` |
| ShopItemRepository | `shop_items` |
| NotificationRepository | `notifications` |

- **Room DB version 14** (destructive migration for dev) — 10 entities
- TaskEntity: supports `category` (String), `recurrenceType` ("none"/"daily"/"weekly"/"monthly"), `recurrenceInterval` (Int)

### Components

- Bottom sheets: `CreateTaskBottomSheet` (create + edit), `AddEventBottomSheet`, `CreateSharedEventBottomSheet`, `CreateGroupBottomSheet`, `SearchFriendBottomSheet`, `InviteToGroupBottomSheet`
- Calendar views: `MonthlyCalendar`, `WeekCalendar`, `DayCalendar`, `YearCalendar`
- Cards: `FriendCard`, `GroupCard`, `SharedEventCard`, `PendingRequestCard`, `OutgoingRequestCard`, `GroupInvitationCard`
- Friends chrome: `FriendsTabSwitcher`, `FriendsTopBar`
- Misc: `ColorPicker`, `DurationPickerDialog`, `PurchaseConfirmDialog`

### Key Features

- Task CRUD with date/time pickers (Material3)
- **Editar tarea** — CreateTaskBottomSheet opens in edit mode with pre-filled fields when editing
- **Tareas recurrentes** — marking a task with `recurrenceType != "none"` as complete auto-creates a new task for the next period
- **Buscar tareas** — search icon in DashboardHeader toggles a text field; filters tasks client-side by title
- **Deshacer eliminar** — delete button in TaskItem removes task and shows Snackbar with undo option (5s window)
- **DurationPickerDialog snap** — initial value correctly centered; user scroll snaps without +4 offset drift
- Calendar with 4 view modes (day / week / month / year)
- Focus timer with configurable duration picker
- Friends, groups, and shared events (local-only)
- User profile with display name, username, avatar emoji
- Dark mode toggle (persisted via DataStore)
- Streak tracking + dynamic achievements system
- Points system + shop (local items)
- Completed tasks collapsed in animated dropdown
- Notifications CRUD with Room + badge on Dashboard bell icon
- Notifications filtered per `userId` (each user sees only their own)
- Achievement unlock detection → auto-generates notification (persisted)
- OctoShop purchase flow (confirm dialog + snackbar); points deducted atomically before marking purchased
- Equip/unequip system for purchased items (shown on profile avatar)
- SQL schema for Supabase sync (`sql/` — 4 files: tables, RLS, triggers, seed)
- **Social registration** — signup with displayName, avatar, @username, password
- **Social login** — username + password with SHA-256 hash verification (`OktodoSalt2026`)
- **Friend search** — live search by displayName/username in `SearchFriendBottomSheet`
- **Friend requests** — send, accept, decline; pending incoming/outgoing sections in Amigos tab
- **Request notifications** — both sides notified on send/receive
- **Demo requests** — 2 seed requests from María García + Sofía Torres on signup
- **Settings screen** — replaces EditProfileScreen; includes logout with confirmation dialog
- **UserProfiles** — Room entity + DAO (findByUsername, search, unique username constraint)
- **Group invitations** — invite friends to a group from GroupDetailScreen; accept/decline in Grupos tab
  - `GroupInvitationEntity` with unique index on `(toUserId, groupId)`
  - Creating a group only adds "Tú"; selected friends receive invitations (not auto-added)
  - Accepting adds the invitee to the group's members list
  - Both sender and recipient receive a notification
- **Group settings** — rename or delete a group from a settings modal inside GroupDetailScreen
- **Group visibility** — groups filtered by membership; only shows groups where current user is a member
- **No hardcoded seed data** — friends, groups, and shared events start empty; 8 demo UserProfiles remain for social search
- **Weekly goal UI** — SettingsScreen "Preferencias" card: progress bar + +/− buttons (range 1–30), saves immediately
- **Weekly reset** — `checkWeeklyReset()` runs on app start; resets `weeklyCompleted` to 0 each new ISO week via `LAST_WEEK_RESET` key in DataStore

---

## Recent Commits (26 May 2026)

```
5d7a800 fix: DurationPickerDialog scrollOffset sign inversion caused +4 offset snap
399ae56 fix: add key(YearMonth) to MonthlyCalendar for proper recomposition on month change
70be306 perf: scoped TasksViewModel y CalendarViewModel al NavBackStackEntry para que solo consuman recursos en pestaña activa
1d2b1ea refactor: replace NavigationBarItem with custom Column+clickable for bottom nav
969e38d fix: remove elevation from completed task cards
5a6119a fix: getGreeting sin remember para que sea dinamico segun la hora
22e3a17 feat: deshacer eliminar - pendingDeletedTask con delay 5s, snackbar Host, undoDelete, delete button en TaskItem
ef92957 fix: show shop items in offline mode
cbdb2bc fix: reactive seed of shop items on userId change
1e4097d refactor: avoid data duplication between tasks and calendar
57dc0e4 fix: isolate shop items per user
8b26ad7 fix: sync tasks and calendar events
b6d8205 feat: buscar tareas - search bar in DashboardHeader, filteredTasks combine flow, setSearchQuery in VM
212124c feat: tareas recurrentes - crear nueva tarea al completar una con recurrenceType != none
c2bff9e feat: editar tarea - CreateTaskBottomSheet con modo edicion, updateTask/editTask en VM, icono de editar en TaskItem
7fddf92 fix: errores de compilacion preexistentes - import @Update faltante, private en DAO, import duplicado, remember faltante
11d8e79 feat: schema categorias + tareas recurrentes, agregar category/recurrenceType/recurrenceInterval a TaskEntity
49dc8b3 fix: Bug1 currentWeekKey usa now.year en vez de ISO year, Bug2 scrollToItem offset negativo
daa0f67 fix: DatePicker en CreateSharedEventBottomSheet abre en 1970, pasar initialSelectedDateMillis
26870b7 fix: eliminar shared event desde calendario no funciona, redirigir segun prefijo shared_
423727a fix: timer no reinicia al llegar a 0, resetear timeLeft al presionar play
bfec714 fix: updateProfile no persiste a Room, agregar @Update en DAO y sync en VM
87ce691 fix: race condition checkWeeklyReset + completeTask, unificar en edit atomico
2f2e10b fix: awaitSeed cuelga forever si seedIfEmpty lanza, agregar try/catch/finally
c48b707 fix: remove unused Date import in NotificationsScreen
b1e043e fix: L1 WeekCalendar O(7n), L3 SimpleDateFormat, L4 time default, L5 key scroll, L6 dead onClick
a56587b fix: M22 SettingsScreen escribe DataStore en cada click, M25 createSharedEvent no transaccional
008b03c fix: M21 pause no cancela timerJob, M23 collectAsState inline, M24 username default en blanco
5d36306 fix: purchaseItem race condition, esperar seedIfEmpty con CompletableDeferred
d960b36 fix: sendFriendRequest sin check de duplicado, usar findExisting antes de insertar
```

---

## What's Missing / Next Up

- **Task categories picker** — UI for selecting/assigning a category when creating/editing a task
- **Cloud sync / Backend** — SQL schema ready (`sql/` folder), pending client implementation
  - Supabase: 12 tables + 1 VIEW + RLS + triggers + seed catalog
  - `user_profiles` + `local_credentials` ready in SQL; cloud auth uses Supabase Auth
- **Tests** — only placeholder tests exist (`ExampleUnitTest`, `ExampleInstrumentedTest`)
  - Turbine, MockK, Room in-memory planned

---

## Build Commands

```bash
# Build
./gradlew assembleDebug
./gradlew assembleRelease

# Test
./gradlew test

# Lint
./gradlew lint
```

> On Windows, use `gradlew.bat` (e.g., `.\gradlew.bat assembleDebug`).
