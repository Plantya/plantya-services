-- =========================================
-- FUNCTION: generate user_id
-- =========================================
CREATE OR REPLACE FUNCTION generate_user_id()
    RETURNS TRIGGER AS $$
BEGIN
    IF NEW.user_id IS NULL THEN
        NEW.user_id := 'U' || LPAD(nextval('user_seq')::TEXT, 5, '0');
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- =========================================
-- TRIGGERS
-- =========================================
CREATE TRIGGER trg_generate_device_id
    BEFORE INSERT ON users
    FOR EACH ROW
EXECUTE FUNCTION generate_user_id();
