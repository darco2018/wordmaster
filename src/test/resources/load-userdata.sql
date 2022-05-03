-- id MUST be given when using SEQUENCE
-- id & email CANNOT be the same as in data.sql
INSERT INTO user (id, email, date_created, date_updated) VALUES (1999, 'testUser1@gmail.com', NOW(), NOW());
INSERT INTO user (id, email, date_created, date_updated) VALUES (2999, 'testUser2@gmail.com', NOW(), NOW());