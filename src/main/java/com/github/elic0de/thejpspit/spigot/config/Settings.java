package com.github.elic0de.thejpspit.spigot.config;

import net.william278.annotaml.YamlComment;
import net.william278.annotaml.YamlFile;
import net.william278.annotaml.YamlKey;

@YamlFile()
public class Settings {

    // Database settings
    /*@YamlComment("Database connection settings")
    @YamlKey("database.type")
    private Database.Type databaseType = Database.Type.SQLITE;*/

    @YamlKey("database.mysql.credentials.host")
    private String mySqlHost = "localhost";

    @YamlKey("database.mysql.credentials.port")
    private int mySqlPort = 3306;

    @YamlKey("database.mysql.credentials.database")
    private String mySqlDatabase = "TheJpsPit";

    @YamlKey("database.mysql.credentials.username")
    private String mySqlUsername = "root";

    @YamlKey("database.mysql.credentials.password")
    private String mySqlPassword = "pa55w0rd";

    @YamlComment("MySQL connection pool properties")
    @YamlKey("database.mysql.connection_pool.size")
    private int mySqlConnectionPoolSize = 10;

    @YamlKey("database.mysql.connection_pool.idle")
    private int mySqlConnectionPoolIdle = 10;

    @YamlKey("database.mysql.connection_pool.lifetime")
    private long mySqlConnectionPoolLifetime = 1800000;

    @YamlKey("database.mysql.connection_pool.keepalive")
    private long mySqlConnectionPoolKeepAlive = 30000;

    @YamlKey("database.mysql.connection_pool.timeout")
    private long mySqlConnectionPoolTimeout = 20000;
}
