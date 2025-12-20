DO $$
    BEGIN
        IF NOT EXISTS (
            SELECT 1
            FROM pg_type t
            JOIN pg_namespace n ON n.oid = t.typnamespace
            WHERE t.typname = 'user_role'
              AND n.nspname = 'public'
        ) THEN
            CREATE TYPE public.user_role AS ENUM ('USER','STAFF','ADMIN');
        END IF;
    END$$;
