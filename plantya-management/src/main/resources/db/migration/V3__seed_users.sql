INSERT INTO users (user_id, email, password, name, role, created_at, updated_at, deleted_at)
VALUES ('admin-001', 'admin@example.com', '$2a$10$hashed_admin_password', 'System Administrator', 'ADMIN', NOW(), NOW(),NULL),
       ('staff-001', 'staff@example.com', '$2a$10$hashed_staff_password', 'Staff User', 'STAFF', NOW(), NOW(), NULL),
       ('user-001', 'user1@example.com', '$2a$10$hashed_user_password', 'Regular User 1', 'USER', NOW(), NOW(), NULL),
       ('user-002', 'user2@example.com', '$2a$10$hashed_user_password', 'Regular User 2', 'USER', NOW(), NOW(), NULL),
       ('user-003', 'user3@example.com', '$2a$10$hashed_user_password', 'Regular User 3', 'USER', NOW(), NOW(), NULL),
       ('staff-002', 'staff2@example.com', '$2a$10$hashed_staff_password', 'Staff User 2', 'STAFF', NOW(), NOW(), NULL),
       ('user-004', 'user4@example.com', '$2a$10$hashed_user_password', 'Regular User 4', 'USER', NOW(), NOW(), NULL),
       ('user-005', 'user5@example.com', '$2a$10$hashed_user_password', 'Regular User 5', 'USER', NOW(), NOW(), NULL),
       ('staff-003', 'staff3@example.com', '$2a$10$hashed_staff_password', 'Staff User 3', 'STAFF', NOW(), NOW(), NULL),
       ('admin-002', 'admin2@example.com', '$2a$10$hashed_admin_password', 'System Administrator 2', 'ADMIN', NOW(),NOW(), NULL)
ON CONFLICT (email) DO NOTHING;
