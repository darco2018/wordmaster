-- id MUST be given when using SEQUENCE
-- id & email CANNOT be the same as in data.sql
INSERT INTO AppUser (userID, email, dateCreated, dateUpdated) VALUES (1999, 'testUser1@gmail.com', NOW(), NOW());
INSERT INTO AppUser (userID, email, dateCreated, dateUpdated) VALUES (2999, 'testUser2@gmail.com', NOW(), NOW());