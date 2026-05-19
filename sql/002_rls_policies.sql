-- 002_rls_policies.sql
-- Oktodo — Row Level Security

-- Habilita RLS en todas las tablas
ALTER TABLE user_profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE tasks ENABLE ROW LEVEL SECURITY;
ALTER TABLE calendar_events ENABLE ROW LEVEL SECURITY;
ALTER TABLE friends ENABLE ROW LEVEL SECURITY;
ALTER TABLE groups ENABLE ROW LEVEL SECURITY;
ALTER TABLE group_members ENABLE ROW LEVEL SECURITY;
ALTER TABLE group_invitations ENABLE ROW LEVEL SECURITY;
ALTER TABLE shared_events ENABLE ROW LEVEL SECURITY;
ALTER TABLE shop_catalog ENABLE ROW LEVEL SECURITY;
ALTER TABLE user_items ENABLE ROW LEVEL SECURITY;
ALTER TABLE notifications ENABLE ROW LEVEL SECURITY;

-- ── user_profiles ──────────────────────────────────────
CREATE POLICY "users can read own profile"
  ON user_profiles FOR SELECT
  USING (auth.uid() = user_id);

CREATE POLICY "users can read any profile by username"
  ON user_profiles FOR SELECT
  USING (true);

CREATE POLICY "users can insert own profile"
  ON user_profiles FOR INSERT
  WITH CHECK (auth.uid() = user_id);

CREATE POLICY "users can update own profile"
  ON user_profiles FOR UPDATE
  USING (auth.uid() = user_id);

-- ── tasks ──────────────────────────────────────────────
CREATE POLICY "users can read own tasks"
  ON tasks FOR SELECT
  USING (auth.uid() = user_id);

CREATE POLICY "users can insert own tasks"
  ON tasks FOR INSERT
  WITH CHECK (auth.uid() = user_id);

CREATE POLICY "users can update own tasks"
  ON tasks FOR UPDATE
  USING (auth.uid() = user_id);

CREATE POLICY "users can delete own tasks"
  ON tasks FOR DELETE
  USING (auth.uid() = user_id);

-- ── calendar_events ────────────────────────────────────
CREATE POLICY "users can read own calendar_events"
  ON calendar_events FOR SELECT
  USING (auth.uid() = user_id);

CREATE POLICY "users can insert own calendar_events"
  ON calendar_events FOR INSERT
  WITH CHECK (auth.uid() = user_id);

CREATE POLICY "users can update own calendar_events"
  ON calendar_events FOR UPDATE
  USING (auth.uid() = user_id);

CREATE POLICY "users can delete own calendar_events"
  ON calendar_events FOR DELETE
  USING (auth.uid() = user_id);

-- ── friends ────────────────────────────────────────────
CREATE POLICY "users can read own friends"
  ON friends FOR SELECT
  USING (auth.uid() = user_id);

CREATE POLICY "users can insert own friends"
  ON friends FOR INSERT
  WITH CHECK (auth.uid() = user_id);

CREATE POLICY "users can update own friends"
  ON friends FOR UPDATE
  USING (auth.uid() = user_id);

CREATE POLICY "users can delete own friends"
  ON friends FOR DELETE
  USING (auth.uid() = user_id);

-- ── groups ─────────────────────────────────────────────
CREATE POLICY "members can read groups"
  ON groups FOR SELECT
  USING (
    EXISTS (
      SELECT 1 FROM group_members
      WHERE group_id = groups.id
        AND user_id = auth.uid()
    )
  );

CREATE POLICY "creator can insert groups"
  ON groups FOR INSERT
  WITH CHECK (auth.uid() = created_by);

CREATE POLICY "creator can update groups"
  ON groups FOR UPDATE
  USING (auth.uid() = created_by);

-- ── group_members ──────────────────────────────────────
CREATE POLICY "members can read group_members"
  ON group_members FOR SELECT
  USING (
    EXISTS (
      SELECT 1 FROM group_members gm
      WHERE gm.group_id = group_members.group_id
        AND gm.user_id = auth.uid()
    )
  );

-- group_members INSERT solo via trigger de invitation accept
-- (no hay policy de INSERT — la tabla solo se modifica via trigger)

CREATE POLICY "creator can delete group_members"
  ON group_members FOR DELETE
  USING (
    EXISTS (
      SELECT 1 FROM groups
      WHERE id = group_members.group_id
        AND created_by = auth.uid()
    )
  );

-- ── group_invitations ──────────────────────────────────
CREATE POLICY "creator can read invitations for own groups"
  ON group_invitations FOR SELECT
  USING (
    EXISTS (
      SELECT 1 FROM groups
      WHERE id = group_invitations.group_id
        AND created_by = auth.uid()
    )
    OR invited_user_id = auth.uid()
  );

CREATE POLICY "creator can send invitations"
  ON group_invitations FOR INSERT
  WITH CHECK (
    EXISTS (
      SELECT 1 FROM groups
      WHERE id = group_invitations.group_id
        AND created_by = auth.uid()
    )
  );

CREATE POLICY "invited user can accept or decline"
  ON group_invitations FOR UPDATE
  USING (invited_user_id = auth.uid() AND status = 'pending')
  WITH CHECK (invited_user_id = auth.uid() AND status IN ('accepted', 'declined'));

-- ── shared_events ──────────────────────────────────────
CREATE POLICY "members can read shared_events"
  ON shared_events FOR SELECT
  USING (
    EXISTS (
      SELECT 1 FROM group_members
      WHERE group_id = shared_events.group_id
        AND user_id = auth.uid()
    )
  );

CREATE POLICY "members can insert shared_events"
  ON shared_events FOR INSERT
  WITH CHECK (
    EXISTS (
      SELECT 1 FROM group_members
      WHERE group_id = shared_events.group_id
        AND user_id = auth.uid()
    )
  );

CREATE POLICY "update shared_events"
  ON shared_events FOR UPDATE
  USING (
    auth.uid() = created_by
    OR EXISTS (
      SELECT 1 FROM groups
      WHERE id = shared_events.group_id
        AND members_can_edit_events = true
        AND EXISTS (
          SELECT 1 FROM group_members
          WHERE group_id = shared_events.group_id
            AND user_id = auth.uid()
        )
    )
  );

CREATE POLICY "delete shared_events"
  ON shared_events FOR DELETE
  USING (auth.uid() = created_by);

-- ── shop_catalog (global, read-only desde la app) ──────
CREATE POLICY "anyone can read shop_catalog"
  ON shop_catalog FOR SELECT
  USING (true);

-- ── user_items ─────────────────────────────────────────
CREATE POLICY "users can read own items"
  ON user_items FOR SELECT
  USING (auth.uid() = user_id);

CREATE POLICY "users can insert own items"
  ON user_items FOR INSERT
  WITH CHECK (auth.uid() = user_id);

CREATE POLICY "users can update own items"
  ON user_items FOR UPDATE
  USING (auth.uid() = user_id);

-- ── notifications ──────────────────────────────────────
CREATE POLICY "users can read own notifications"
  ON notifications FOR SELECT
  USING (auth.uid() = user_id);

CREATE POLICY "users can insert own notifications"
  ON notifications FOR INSERT
  WITH CHECK (auth.uid() = user_id);

CREATE POLICY "users can update own notifications"
  ON notifications FOR UPDATE
  USING (auth.uid() = user_id);

CREATE POLICY "users can delete own notifications"
  ON notifications FOR DELETE
  USING (auth.uid() = user_id);
