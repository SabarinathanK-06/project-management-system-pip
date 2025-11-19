DO
$$
DECLARE
    admin_role_id UUID;
    admin_user_id UUID;
BEGIN
    SELECT id
    INTO admin_role_id
    FROM roles
    WHERE UPPER(name) = 'ADMIN'
      AND (is_deleted IS NULL OR is_deleted = FALSE)
    LIMIT 1;

    IF admin_role_id IS NULL THEN
        RAISE EXCEPTION 'ADMIN role not found. Ensure V1 migration ran.';
    END IF;

    SELECT id
    INTO admin_user_id
    FROM pm_users
    WHERE email = 'admin@i2i.com'
    LIMIT 1;

    IF admin_user_id IS NULL THEN
        admin_user_id := gen_random_uuid();
        INSERT INTO pm_users(id, email, password, first_name, last_name, phone_number, address, is_deleted)
        VALUES (
            admin_user_id,
            'admin@i2i.com',
            crypt('Admin@123', gen_salt('bf')),
            'System',
            'Admin',
            '+1-000-000-0000',
            'HQ',
            FALSE
        );
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM user_roles WHERE user_id = admin_user_id AND role_id = admin_role_id
    ) THEN
        INSERT INTO user_roles(user_id, role_id) VALUES (admin_user_id, admin_role_id);
    END IF;
END
$$;

