DO $$
    BEGIN
        IF NOT EXISTS (
            SELECT 1
            FROM pg_type
            WHERE typname = 'user_role'
        ) THEN
    CREATE TYPE user_role AS ENUM (
                'USER',
                'STAFF',
                'ADMIN'
            );
    END IF;
    END
$$;