CREATE TABLE IF NOT EXISTS AppUser (
    userID        BIGINT(20)   NOT NULL PRIMARY KEY,
    dateCreated   DATETIME(6)  NOT NULL,
    dateUpdated   DATETIME(6)  NOT NULL,
    email         VARCHAR(100) NOT NULL UNIQUE
)   ENGINE=INNODB;

CREATE TABLE IF NOT EXISTS AppUserSeq (
    next_val BIGINT(20)
)   ENGINE=INNODB;

INSERT INTO AppUserSeq values (100);

CREATE TABLE IF NOT EXISTS HeadlineExercise (
    headlineExerciseID    BIGINT(20)   NOT NULL PRIMARY KEY,
    content               VARCHAR(255) NOT NULL,
    dateCreated           DATETIME(6)  NOT NULL,
    dateUpdated           DATETIME(6)  NOT NULL,
    title                 VARCHAR(255) NOT NULL,
    userID                BIGINT(20)
)   ENGINE=INNODB;

CREATE TABLE IF NOT EXISTS HeadlineExerciseSeq (
    next_val BIGINT(20)
)   ENGINE=INNODB;

INSERT INTO HeadlineExerciseSeq values ( 10 );
