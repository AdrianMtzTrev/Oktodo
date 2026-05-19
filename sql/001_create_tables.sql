-- 001_create_tables.sql
-- Oktodo — Supabase schema

-- ── user_profiles ──────────────────────────────────────
CREATE TABLE user_profiles (
  user_id          UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
  display_name     TEXT NOT NULL DEFAULT 'Usuario OKTodo',
  username         TEXT NOT NULL,
  avatar_emoji     TEXT NOT NULL DEFAULT '🐙',
  points           INT NOT NULL DEFAULT 0,
  completed_tasks  INT NOT NULL DEFAULT 0,
  streak_days      INT NOT NULL DEFAULT 0,
  weekly_goal      INT NOT NULL DEFAULT 10,
  weekly_completed INT NOT NULL DEFAULT 0,
  last_active_date DATE,
  created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);

ALTER TABLE user_profiles ADD CONSTRAINT uq_username UNIQUE (username);
CREATE INDEX idx_user_profiles_username ON user_profiles (username);

-- ── tasks ──────────────────────────────────────────────
CREATE TABLE tasks (
  id            TEXT PRIMARY KEY,
  user_id       UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  title         TEXT NOT NULL,
  time          TEXT NOT NULL,
  priority      TEXT NOT NULL,
  color_argb    BIGINT NOT NULL,
  is_completed  BOOLEAN NOT NULL DEFAULT false,
  points_reward INT NOT NULL DEFAULT 10,
  created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
  is_deleted    BOOLEAN NOT NULL DEFAULT false
);

CREATE INDEX idx_tasks_user ON tasks (user_id);
CREATE INDEX idx_tasks_updated ON tasks (updated_at);

-- ── calendar_events ────────────────────────────────────
CREATE TABLE calendar_events (
  id          TEXT PRIMARY KEY,
  user_id     UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  title       TEXT NOT NULL,
  date_string TEXT NOT NULL,
  time_string TEXT,
  location    TEXT,
  color_argb  BIGINT NOT NULL,
  description TEXT,
  created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
  is_deleted  BOOLEAN NOT NULL DEFAULT false
);

CREATE INDEX idx_calendar_events_user ON calendar_events (user_id);
CREATE INDEX idx_calendar_events_updated ON calendar_events (updated_at);

-- ── friends ────────────────────────────────────────────
CREATE TABLE friends (
  id                TEXT PRIMARY KEY,
  user_id           UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  name              TEXT NOT NULL,
  points            INT NOT NULL DEFAULT 0,
  events            INT NOT NULL DEFAULT 0,
  avatar            TEXT,
  avatar_color_argb BIGINT NOT NULL,
  created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
  is_deleted        BOOLEAN NOT NULL DEFAULT false
);

CREATE INDEX idx_friends_user ON friends (user_id);
CREATE INDEX idx_friends_updated ON friends (updated_at);

-- ── groups ─────────────────────────────────────────────
CREATE TABLE groups (
  id                      TEXT PRIMARY KEY,
  name                    TEXT NOT NULL,
  icon                    TEXT NOT NULL DEFAULT '👥',
  created_by              UUID NOT NULL REFERENCES auth.users(id),
  members_can_edit_events BOOLEAN NOT NULL DEFAULT false,
  created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at              TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_groups_created_by ON groups (created_by);

-- ── group_members ──────────────────────────────────────
CREATE TABLE group_members (
  group_id  TEXT NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
  user_id   UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  joined_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  PRIMARY KEY (group_id, user_id)
);

CREATE INDEX idx_group_members_user ON group_members (user_id);

-- ── group_invitations ──────────────────────────────────
CREATE TABLE group_invitations (
  id              TEXT PRIMARY KEY,
  group_id        TEXT NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
  invited_user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  invited_by      UUID NOT NULL REFERENCES auth.users(id),
  status          TEXT NOT NULL DEFAULT 'pending'
                  CHECK (status IN ('pending', 'accepted', 'declined')),
  created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX idx_group_invitations_pending
  ON group_invitations (group_id, invited_user_id)
  WHERE status = 'pending';

-- Auto-insert into group_members when invitation is accepted
CREATE OR REPLACE FUNCTION accept_group_invitation()
RETURNS TRIGGER AS $$
BEGIN
  INSERT INTO group_members (group_id, user_id)
  VALUES (NEW.group_id, NEW.invited_user_id);
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_group_invitations_accept
  AFTER UPDATE ON group_invitations
  FOR EACH ROW
  WHEN (NEW.status = 'accepted' AND OLD.status = 'pending')
  EXECUTE FUNCTION accept_group_invitation();

-- ── shared_events ──────────────────────────────────────
CREATE TABLE shared_events (
  id           TEXT PRIMARY KEY,
  group_id     TEXT NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
  created_by   UUID NOT NULL REFERENCES auth.users(id),
  title        TEXT NOT NULL,
  date         TEXT NOT NULL,
  time         TEXT,
  location     TEXT,
  participants UUID[] NOT NULL DEFAULT '{}',
  created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_shared_events_group ON shared_events (group_id);
CREATE INDEX idx_shared_events_created_by ON shared_events (created_by);

CREATE VIEW shared_events_with_creator AS
SELECT
  se.*,
  creator.display_name AS creator_name,
  creator.avatar_emoji AS creator_avatar
FROM shared_events se
JOIN user_profiles creator ON se.created_by = creator.user_id;

-- ── shop_catalog (global) ──────────────────────────────
CREATE TABLE shop_catalog (
  id       TEXT PRIMARY KEY,
  title    TEXT NOT NULL,
  emoji    TEXT NOT NULL,
  price    INT NOT NULL,
  category TEXT NOT NULL
);

-- ── user_items ─────────────────────────────────────────
CREATE TABLE user_items (
  user_id      UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  item_id      TEXT NOT NULL REFERENCES shop_catalog(id),
  is_purchased BOOLEAN NOT NULL DEFAULT false,
  created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
  PRIMARY KEY (user_id, item_id)
);

CREATE INDEX idx_user_items_user ON user_items (user_id);

-- ── notifications ──────────────────────────────────────
CREATE TABLE notifications (
  id         TEXT PRIMARY KEY,
  user_id    UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  title      TEXT NOT NULL,
  message    TEXT NOT NULL,
  icon       TEXT NOT NULL,
  is_read    BOOLEAN NOT NULL DEFAULT false,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  is_deleted BOOLEAN NOT NULL DEFAULT false
);

CREATE INDEX idx_notifications_user ON notifications (user_id);
CREATE INDEX idx_notifications_created ON notifications (created_at DESC);
