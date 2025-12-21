CREATE INDEX IF NOT EXISTS idx_users_user_id
    ON users(user_id);

CREATE INDEX IF NOT EXISTS idx_users_email
    ON users(email);

CREATE INDEX IF NOT EXISTS idx_users_deleted_at
    ON users(deleted_at);

CREATE INDEX IF NOT EXISTS idx_users_active
    ON users(id)
    WHERE deleted_at IS NULL;