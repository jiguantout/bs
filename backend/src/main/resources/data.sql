-- Default admin user (password: admin123, BCrypt encoded)
INSERT INTO users (username, password, nickname, role, points, status, create_time, update_time)
SELECT 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 'ADMIN', 0, 1, NOW(), NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');
