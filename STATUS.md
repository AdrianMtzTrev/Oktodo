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

- **Active branch:** `develop` (ahead of `main`)
- **Last activity:** 19 May 2026 — social features (signup/login, friend requests, search, notifications per user)
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
| Notifications | `notifications` | NotificationViewModel |

### Data Layer

| Repository | Entities |
|---|---|
| TaskRepository | `tasks` |
| CalendarEventRepository | `calendar_events` |
| FriendsRepository | `friends`, `groups`, `shared_events`, `user_profiles`, `friend_requests` |
| ShopItemRepository | `shop_items` |
| NotificationRepository | `notifications` |

### Components

- Bottom sheets: `CreateTaskBottomSheet`, `AddEventBottomSheet`, `AddEventDialog`, `CreateSharedEventBottomSheet`, `CreateGroupBottomSheet`, `SearchFriendBottomSheet`
- Calendar views: `MonthlyCalendar`, `WeekCalendar`, `DayCalendar`, `YearCalendar`
- Cards: `FriendCard`, `GroupCard`, `SharedEventCard`, `PendingRequestCard`, `OutgoingRequestCard`
- Friends chrome: `FriendsTabSwitcher`, `FriendsTopBar`
- Misc: `ColorPicker`, `DurationPickerDialog`, `PurchaseConfirmDialog`

### Key Features

- Task CRUD with date/time pickers (Material3)
- Calendar with 4 view modes (day / week / month / year)
- Focus timer with configurable duration picker
- Friends, groups, and shared events (local-only)
- User profile with display name, username, avatar emoji
- Dark mode toggle (persisted via DataStore)
- Streak tracking + dynamic achievements system
- Points system + shop (local items)
- Completed tasks collapsed in animated dropdown
- Notifications CRUD with Room + badge on Dashboard bell icon
- Notifications **filtered per user** (each user sees only their own notifications)
- Achievement unlock detection → auto-generates notification (persisted)
- OctoShop purchase flow (confirm dialog + snackbar feedback)
- Equip/unequip system for purchased items (shown on profile avatar)
- SQL schema for Supabase sync (`sql/` — 4 files: tables, RLS, triggers, seed)
- **Social registration** — signup with displayName, avatar, @username, password
- **Social login** — username + password with SHA-256 hash verification
- **Password hashing** — SHA-256 with fixed salt (`OktodoSalt2026`)
- **Friend search** — live search by displayName/username in SearchFriendBottomSheet
- **Friend requests** — send, accept, decline, pending incoming/outgoing sections
- **Request notifications** — sender sees "Solicitud enviada a @target"; receiver sees "{name} quiere ser tu amigo"
- **Demo requests** — 2 seed requests from María García + Sofía Torres on signup
- **Settings screen** — replaces EditProfileScreen; includes logout with confirmation dialog
- **UserProfiles** — Room entity + DAO (findByUsername, search, unique username)
- DB version 7 (destructive migration for dev)

---

## Recent Commits (19 May 2026)

```
2d1dedc fix: per-user notification filtering with userId
c6d123d feat: friend requests with notifications
307a29a feat: signup ahora pide nombre + avatar + username + contraseña
358f931 feat: SettingsScreen replaces EditProfileScreen with logout
9c313df fix: bump DB version to 5 (passwordHash column added to user_profiles)
4ffce66 feat: password-based signup/login for social features
eefb21e feat: add local_credentials table (password hash) to SQL schema
6012efd feat: search friends UI with SearchFriendBottomSheet
ab1c0b4 feat: social registration flow (local login/signup in FriendsScreen)
df29ac8 feat: friend_requests table + friends as relationships (same pattern as group_invitations)
8f6588e feat: Supabase SQL schema with 11 tables and invitations system
81cf37b feat: OctoShop purchase flow with confirm dialog, snackbar and equip system
9befe0c feat: auto-generate notifications when achievements unlock
38c76fe feat: notifications system with CRUD, screen and bell icon
```

---

## What's Missing / Next Up

- **Cloud sync / Backend** — SQL schema ready (`sql/` folder), pending client implementation
  - Supabase: 12 tables + 1 VIEW + RLS + triggers + seed catalog
  - `user_profiles` + `local_credentials` ready in SQL; cloud auth uses Supabase Auth
- **Group invitations from UI** — same pattern as friend requests (`GroupInvitationEntity` → accept trigger)
- **Real purchase flow** — deduct points from DataStore on purchase (currently placeholder)
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
