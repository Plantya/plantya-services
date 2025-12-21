CREATE OR REPLACE FUNCTION set_user_id()
    RETURNS TRIGGER AS $$
BEGIN
    IF NEW.user_id IS NULL
        THEN NEW.user_id := SUBSTRING(NEW.role::text, 1, 1) || LPAD(nextval('user_code_seq')::text, 5, '0');
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;