CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(16) UNIQUE NOT NULL DEFAULT
        (
            CONCAT(
                SUBSTRING(role::text, 1, 1),
                LPAD(nextval('user_code_seq')::text, 5, '0')
            )
        ),    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255),
    name VARCHAR(255),
    role user_role NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_users_user_id
    ON users(user_id);

CREATE INDEX IF NOT EXISTS idx_users_email
    ON users(email);

CREATE INDEX IF NOT EXISTS idx_users_deleted_at
    ON users(deleted_at);

CREATE INDEX IF NOT EXISTS idx_users_active
    ON users(id)
    WHERE deleted_at IS NULL;