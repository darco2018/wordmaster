DELETE from AppUser;

INSERT INTO AppUser
VALUES (333, '2022-05-01 18:57:35.561892', '2022-05-01 18:57:35.561892', 'dataSQLUser1@onet.com');

INSERT INTO AppUser (userID, email, dateCreated, dateUpdated)
VALUES (444, 'dataSQLUser2@onet.com', NOW(), NOW());