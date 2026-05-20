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
- **Last activity:** 19 May 2026 — weekly goal UI + reset logic, group invitations, group settings, purchase flow fix, seed cleanup
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

### Data Layer

| Repository | Entities |
|---|---|
| TaskRepository | `tasks` |
| CalendarEventRepository | `calendar_events` |
| FriendsRepository | `friends`, `groups`, `shared_events`, `user_profiles`, `friend_requests`, `group_invitations` |
| ShopItemRepository | `shop_items` |
| NotificationRepository | `notifications` |

- **Room DB version 8** (destructive migration for dev) — 10 entities total

### Components

- Bottom sheets: `CreateTaskBottomSheet`, `AddEventBottomSheet`, `AddEventDialog`, `CreateSharedEventBottomSheet`, `CreateGroupBottomSheet`, `SearchFriendBottomSheet`, `InviteToGroupBottomSheet`
- Calendar views: `MonthlyCalendar`, `WeekCalendar`, `DayCalendar`, `YearCalendar`
- Cards: `FriendCard`, `GroupCard`, `SharedEventCard`, `PendingRequestCard`, `OutgoingRequestCard`, `GroupInvitationCard`
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

## Recent Commits (19 May 2026)

```
5b9e839 feat: weekly goal UI with reset logic
2a746df feat: group invitations, group settings, real purchase flow, remove seed data
2d1dedc fix: per-user notification filtering with userId
c6d123d feat: friend requests with notifications
307a29a feat: signup ahora pide nombre + avatar + username + contraseña
358f931 feat: SettingsScreen replaces EditProfileScreen with logout
4ffce66 feat: password-based signup/login for social features
6012efd feat: search friends UI with SearchFriendBottomSheet
ab1c0b4 feat: social registration flow (local login/signup in FriendsScreen)
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
