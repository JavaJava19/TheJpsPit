# Set the storage engine
SET DEFAULT_STORAGE_ENGINE = INNODB;

# Enable foreign key constraints
SET FOREIGN_KEY_CHECKS = 1;

# Create the users table if it does not exist
CREATE TABLE IF NOT EXISTS `%user_data%`
(
    `uuid`              char(36)    NOT NULL UNIQUE PRIMARY KEY,
    `username`          varchar(16) NOT NULL,
    `kills`             integer     NOT NULL DEFAULT 0,
    `streaks`           integer     NOT NULL DEFAULT 0,
    `bestStreaks`       integer     NOT NULL DEFAULT 0,
    `deaths`            integer     NOT NULL DEFAULT 0,
    `rating`            integer     NOT NULL DEFAULT 0,
    `bestRating`        integer     NOT NULL DEFAULT 0,
    `xp`                integer     NOT NULL DEFAULT 0,
    `preferences`       longblob    NOT NULL,
) CHARACTER SET utf8
  COLLATE utf8_unicode_ci;

# Create the pit table if it does not exist
CREATE TABLE IF NOT EXISTS `%pit_data%`
(
    `id`   int         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `preferences` longblob    NOT NULL
) CHARACTER SET utf8
  COLLATE utf8_unicode_ci;
