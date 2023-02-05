package com.github.elic0de.thejpspit.config;

import com.github.elic0de.thejpspit.database.Database;
import com.github.elic0de.thejpspit.database.Database.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.william278.annotaml.YamlComment;
import net.william278.annotaml.YamlFile;
import net.william278.annotaml.YamlKey;
import org.bukkit.ChatColor;

@YamlFile()
public class Settings {

    @YamlKey("github_token")
    @YamlComment("アクセストークンが漏洩すると、被害が広範囲に及ぶことになりますので公開しないでください")
    private String githubToken = "";

    // Database settings
    @YamlComment("Database connection settings")
    @YamlKey("database.type")
    private Database.Type databaseType = Database.Type.SQLITE;

    @YamlKey("database.mysql.credentials.host")
    private String mySqlHost = "localhost";

    @YamlKey("database.mysql.credentials.port")
    private int mySqlPort = 3306;

    @YamlKey("database.mysql.credentials.database")
    private String mySqlDatabase = "Pit";

    @YamlKey("database.mysql.credentials.username")
    private String mySqlUsername = "root";

    @YamlKey("database.mysql.credentials.password")
    private String mySqlPassword = "pa55w0rd";

    @YamlKey("database.mysql.credentials.parameters")
    private String mySqlConnectionParameters = "?autoReconnect=true&useSSL=false&useUnicode=true&characterEncoding=UTF-8";

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

    @YamlKey("database.table_names")
    private Map<String, String> tableNames = Map.of(
        Database.Table.USER_DATA.name().toLowerCase(), Database.Table.USER_DATA.getDefaultName(),
        Database.Table.PIT_DATA.name().toLowerCase(), Database.Table.PIT_DATA.getDefaultName()
    );

    @YamlKey("scoreboard")
    private List<String> scoreboard = Arrays.asList(
        "",
        "レベル: [%level%]",
        "JP: [%coins%]",
        "",
        "K/Dレート: &c%rating%",
        "最高レート: &b%bestRating%",
        "",
        "次のレベルまで：&a%neededXp%",
        "",
        "連続キル数: &a%streaks%",
        "最高連続キル数: &a%bestStreaks%",
        "",
        "&ejapanpvpserver.net"
    );

    @YamlKey("level")
    private List<String> level = Arrays.asList(
        "1,15,15," + ChatColor.GRAY.name(),
        "10,30,165," + ChatColor.BLUE.name(),
        "20,50,480," + ChatColor.DARK_AQUA.name(),
        "30,75,1000," + ChatColor.DARK_GREEN.name(),
        "40,125,1775," + ChatColor.GREEN.name(),
        "50,250,4550," + ChatColor.YELLOW.name(),
        "60,600,10800," + ChatColor.GOLD.name(),
        "70,800,16000," + ChatColor.RED.name(),
        "80,900,28000," + ChatColor.DARK_RED.name(),
        "90,1000,381000," + ChatColor.AQUA.name()
    );

    public String getGithubToken() {
        return githubToken;
    }

    public Type getDatabaseType() {
        return databaseType;
    }

    public String getMySqlHost() {
        return mySqlHost;
    }

    public int getMySqlPort() {
        return mySqlPort;
    }

    public String getMySqlDatabase() {
        return mySqlDatabase;
    }

    public String getMySqlUsername() {
        return mySqlUsername;
    }

    public String getMySqlPassword() {
        return mySqlPassword;
    }

    public String getMySqlConnectionParameters() {
        return mySqlConnectionParameters;
    }

    public int getMySqlConnectionPoolSize() {
        return mySqlConnectionPoolSize;
    }

    public int getMySqlConnectionPoolIdle() {
        return mySqlConnectionPoolIdle;
    }

    public long getMySqlConnectionPoolLifetime() {
        return mySqlConnectionPoolLifetime;
    }

    public long getMySqlConnectionPoolKeepAlive() {
        return mySqlConnectionPoolKeepAlive;
    }

    public long getMySqlConnectionPoolTimeout() {
        return mySqlConnectionPoolTimeout;
    }

    public String getTableName(Database.Table tableName) {
        return Optional.ofNullable(tableNames.get(tableName.name().toLowerCase())).orElse(tableName.getDefaultName());
    }

    public List<String> getScoreboard() {
        return scoreboard;
    }

    public List<String> getLevel() {
        return level;
    }
}
