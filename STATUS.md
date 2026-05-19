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
- **Last activity:** 19 May 2026 — notifications, achievements, OctoShop purchase flow
- **Working tree:** clean (only untracked files are CLAUDE.md and STATUS.md)

### Branches

| Branch | Details |
|---|---|
| `main` | Stable / release |
| `develop` | Active development |
| `gh-pages` | GitHub Pages (landing page) |

---

## What's Implemented

### Screens (9)

| Screen | Route | ViewModel |
|---|---|---|
| Dashboard | `dashboard` | TasksViewModel |
| Calendar | `calendario` | CalendarViewModel |
| Focus | `focus` | None (local state) |
| Friends | `friends` | FriendsViewModel |
| Group Detail | `group_detail/{groupId}` | FriendsViewModel |
| Profile | `profile` | ProfileViewModel |
| Edit Profile | `edit_profile` | ProfileViewModel |
| Octo Shop | `octo_shop` | ProfileViewModel |
| Notifications | `notifications` | NotificationViewModel |

### Data Layer

| Repository | Entities |
|---|---|
| TaskRepository | `tasks` |
| CalendarEventRepository | `calendar_events` |
| FriendsRepository | `friends`, `groups`, `shared_events` |
| ShopItemRepository | `shop_items` |
| NotificationRepository | `notifications` |

### Components

- Bottom sheets: `CreateTaskBottomSheet`, `AddEventBottomSheet`, `AddEventDialog`, `CreateSharedEventBottomSheet`, `CreateGroupBottomSheet`
- Calendar views: `MonthlyCalendar`, `WeekCalendar`, `DayCalendar`, `YearCalendar`
- Cards: `FriendCard`, `GroupCard`, `SharedEventCard`
- Friends chrome: `FriendsTabSwitcher`, `FriendsTopBar`
- Misc: `ColorPicker`, `DurationPickerDialog`

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
- Achievement unlock detection → auto-generates notification (persisted)
- OctoShop purchase flow (confirm dialog + snackbar feedback)
- Equip/unequip system for purchased items (shown on profile avatar)
- DB version 3 (destructive migration for dev)
- SQL schema for Supabase sync (`sql/` — 4 files: tables, RLS, triggers, seed)

---

## Recent Commits (19 May 2026)

```
feat: notifications system with full CRUD (Room entity, DAO, repository, ViewModel, screen)
feat: achievement unlock detection → auto-generates notification
feat: notification bell with unread badge on Dashboard + Profile header
feat: click to mark as read, mark all as read, clear read notifications
feat: OctoShop purchase flow with confirmation dialog + snackbar feedback
feat: equip/unequip items system (avatar shows equipped item)
feat: isEquipped field in ShopItemEntity (DB v2→3)
fix: hiltViewModel() in NotificationsScreen (fix crash)
```

---

## What's Missing / Next Up

- **Social / Multiplayer** — share tasks, compete with friends, real-time interaction (Friends screen exists but data is fully local)
- **Cloud sync / Backend** — SQL schema ready (`sql/` folder), pending client implementation
  - Supabase: 12 tables + 1 VIEW + RLS + triggers + seed catalog
  - `friend_requests` → `friends` (mismo patrón que `group_invitations` → `group_members`)
  - `group_invitations` system: creator invites, user accepts/declines, auto-join via trigger
- **Tests** — only placeholder tests exist (`ExampleUnitTest`, `ExampleInstrumentedTest`)

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
