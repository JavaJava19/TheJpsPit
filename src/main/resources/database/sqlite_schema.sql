CREATE TABLE IF NOT EXISTS `%players_table%`
(
    `uuid`              char(36)    NOT NULL UNIQUE,
    `username`          varchar(16) NOT NULL,
    `kills`             integer     NOT NULL DEFAULT 0,
    `streaks`           integer     NOT NULL DEFAULT 0,
    `deaths`            integer     NOT NULL DEFAULT 0,
    `rating`            integer     NOT NULL DEFAULT 0,
    `xp`                integer     NOT NULL DEFAULT 0,

    PRIMARY KEY (`uuid`)
);