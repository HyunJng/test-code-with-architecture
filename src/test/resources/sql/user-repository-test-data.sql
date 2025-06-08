INSERT INTO `users`(`id`, `email`, `nickname`, `address`, `certification_code`, `status`, `last_login_at`)
VALUES(1, 'test101@naver.com', 'test101', 'Seoul', 'aaaaa-aaaaa-aaaaa', 'ACTIVE', 0);
INSERT INTO `users`(`id`, `email`, `nickname`, `address`, `certification_code`, `status`, `last_login_at`)
VALUES(2, 'test102@naver.com', 'test102', 'Seoul', 'bbbbb-bbbbb-bbbbb', 'PENDING', 0);
ALTER TABLE users ALTER COLUMN id RESTART WITH 3;
