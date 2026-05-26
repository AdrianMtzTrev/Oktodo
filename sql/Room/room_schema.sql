-- room_schema.sql
-- Oktodo — Room (SQLite) local schema
-- Generado a partir de las @Entity de Room
-- SQLite DDL (no PostgreSQL, no Supabase)

-- ── tasks ────────────────────────────────────────────────
CREATE TABLE tasks (
    id               TEXT    NOT NULL PRIMARY KEY,
    title            TEXT    NOT NULL,
    time             TEXT    NOT NULL,
    priority         TEXT    NOT NULL,
    colorArgb        INTEGER NOT NULL,
    isCompleted      INTEGER NOT NULL DEFAULT 0,  -- 0=false, 1=true
    pointsReward     INTEGER NOT NULL,
    category         TEXT    NOT NULL DEFAULT '',
    recurrenceType   TEXT    NOT NULL DEFAULT 'none',
    recurrenceInterval INTEGER NOT NULL DEFAULT 1,
    dateString       TEXT    NOT NULL             -- yyyy-MM-dd, default today
);

-- ── calendar_events ──────────────────────────────────────
CREATE TABLE calendar_events (
    id          TEXT    NOT NULL PRIMARY KEY,
    title       TEXT    NOT NULL,
    dateString  TEXT    NOT NULL,
    timeString  TEXT,            -- nullable
    location    TEXT,            -- nullable
    colorArgb   INTEGER NOT NULL,
    description TEXT             -- nullable
);

-- ── friends ──────────────────────────────────────────────
CREATE TABLE friends (
    id              TEXT    NOT NULL PRIMARY KEY,
    name            TEXT    NOT NULL,
    points          INTEGER NOT NULL,
    events          INTEGER NOT NULL,
    avatar          TEXT    NOT NULL,
    avatarColorArgb INTEGER NOT NULL
);

-- ── groups ──────────────────────────────────────────────
CREATE TABLE groups (
    id              TEXT    NOT NULL PRIMARY KEY,
    name            TEXT    NOT NULL,
    icon            TEXT    NOT NULL,
    membersJoined   TEXT    NOT NULL,       -- comma-joined display names
    memberIdsJoined TEXT    NOT NULL DEFAULT '',
    eventCount      INTEGER NOT NULL,
    creatorId       TEXT    NOT NULL DEFAULT ''
);

-- ── shared_events ───────────────────────────────────────
CREATE TABLE shared_events (
    id                TEXT    NOT NULL PRIMARY KEY,
    title             TEXT    NOT NULL,
    creator           TEXT    NOT NULL,       -- display name
    creatorId         TEXT    NOT NULL DEFAULT '',
    groupId           TEXT    NOT NULL,
    date              TEXT    NOT NULL,
    time              TEXT    NOT NULL,
    location          TEXT    NOT NULL,
    participantsJoined TEXT   NOT NULL,       -- comma-joined display names
    canEditJoined     TEXT    NOT NULL DEFAULT ''
);

-- ── shop_items ──────────────────────────────────────────
CREATE TABLE shop_items (
    id          TEXT    NOT NULL PRIMARY KEY,
    title       TEXT    NOT NULL,
    emoji       TEXT    NOT NULL,
    price       INTEGER NOT NULL,
    category    TEXT    NOT NULL,
    isPurchased INTEGER NOT NULL,            -- 0=false, 1=true
    isEquipped  INTEGER NOT NULL DEFAULT 0   -- 0=false, 1=true
);

-- ── notifications ───────────────────────────────────────
CREATE TABLE notifications (
    id        TEXT    NOT NULL PRIMARY KEY,
    userId    TEXT    NOT NULL,
    title     TEXT    NOT NULL,
    message   TEXT    NOT NULL,
    icon      TEXT    NOT NULL,
    isRead    INTEGER NOT NULL,              -- 0=false, 1=true
    createdAt INTEGER NOT NULL               -- epoch millis
);

-- ── user_profiles ───────────────────────────────────────
CREATE TABLE user_profiles (
    id          TEXT    NOT NULL PRIMARY KEY,
    displayName TEXT    NOT NULL,
    username    TEXT    NOT NULL,
    avatarEmoji TEXT    NOT NULL,
    passwordHash TEXT   NOT NULL,
    createdAt   INTEGER NOT NULL             -- epoch millis
);

CREATE UNIQUE INDEX idx_user_profiles_username ON user_profiles (username);

-- ── friend_requests ─────────────────────────────────────
CREATE TABLE friend_requests (
    id              TEXT    NOT NULL PRIMARY KEY,
    fromUserId      TEXT    NOT NULL,
    toUserId        TEXT    NOT NULL,
    fromDisplayName TEXT    NOT NULL,
    fromAvatarEmoji TEXT    NOT NULL,
    toDisplayName   TEXT    NOT NULL,
    toAvatarEmoji   TEXT    NOT NULL,
    status          TEXT    NOT NULL,         -- pending / accepted / declined
    createdAt       INTEGER NOT NULL          -- epoch millis
);

CREATE UNIQUE INDEX idx_friend_requests_from_to ON friend_requests (fromUserId, toUserId);

-- ── group_invitations ───────────────────────────────────
CREATE TABLE group_invitations (
    id              TEXT    NOT NULL PRIMARY KEY,
    fromUserId      TEXT    NOT NULL,
    fromDisplayName TEXT    NOT NULL,
    toUserId        TEXT    NOT NULL,
    toDisplayName   TEXT    NOT NULL,
    groupId         TEXT    NOT NULL,
    groupName       TEXT    NOT NULL,
    groupIcon       TEXT    NOT NULL,
    status          TEXT    NOT NULL,         -- pending / accepted / declined
    createdAt       INTEGER NOT NULL          -- epoch millis
);

CREATE UNIQUE INDEX idx_group_invitations_to_group ON group_invitations (toUserId, groupId);
