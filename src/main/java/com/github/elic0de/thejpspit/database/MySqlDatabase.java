package com.github.elic0de.thejpspit.database;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.config.PitPreferences;
import com.github.elic0de.thejpspit.config.Settings;
import com.github.elic0de.thejpspit.player.OfflinePitPlayer;
import com.github.elic0de.thejpspit.player.PitPlayer;
import com.github.elic0de.thejpspit.player.Preferences;
import com.zaxxer.hikari.HikariDataSource;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MySqlDatabase extends Database {

    /**
     * Name of the Hikari connection pool
     */
    private static final String DATA_POOL_NAME = "MySqlHikariPool";

    /**
     * The Hikari data source
     */
    private HikariDataSource dataSource;

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    private void setConnection() {
        final Settings settings = plugin.getSettings();

        // Create jdbc driver connection url
        final String jdbcUrl = "jdbc:mysql://" + settings.getMySqlHost() + ":" + settings.getMySqlPort() + "/"
            + settings.getMySqlDatabase() + settings.getMySqlConnectionParameters();
        dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(jdbcUrl);

        // Authenticate
        dataSource.setUsername(settings.getMySqlUsername());
        dataSource.setPassword(settings.getMySqlPassword());

        // Set connection pool options
        dataSource.setMaximumPoolSize(settings.getMySqlConnectionPoolSize());
        dataSource.setMinimumIdle(settings.getMySqlConnectionPoolIdle());
        dataSource.setMaxLifetime(settings.getMySqlConnectionPoolLifetime());
        dataSource.setKeepaliveTime(settings.getMySqlConnectionPoolKeepAlive());
        dataSource.setConnectionTimeout(settings.getMySqlConnectionPoolTimeout());
        dataSource.setPoolName(DATA_POOL_NAME);

        // Set additional connection pool properties
        dataSource.setDataSourceProperties(new Properties() {{
            put("cachePrepStmts", "true");
            put("prepStmtCacheSize", "250");
            put("prepStmtCacheSqlLimit", "2048");
            put("useServerPrepStmts", "true");
            put("useLocalSessionState", "true");
            put("useLocalTransactionState", "true");
            put("rewriteBatchedStatements", "true");
            put("cacheResultSetMetadata", "true");
            put("cacheServerConfiguration", "true");
            put("elideSetAutoCommits", "true");
            put("maintainTimeStats", "false");
        }});
    }

    public MySqlDatabase(TheJpsPit plugin) {
        super(plugin, "mysql_schema.sql");
    }

    @Override
    public void initialize() throws RuntimeException {
        // Establish connection
        this.setConnection();

        // Create tables
        try (Connection connection = getConnection()) {
            try (Statement statement = connection.createStatement()) {
                for (String tableCreationStatement : getSchema()) {
                    statement.execute(tableCreationStatement);
                }
            }
            setLoaded(true);
        } catch (SQLException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to create MySQL database tables");
            setLoaded(false);
        }
    }

    @Override
    public Optional<PitPlayer> getPitPlayer(Player player) {
        return getPitPlayer(player.getUniqueId());
    }

    @Override
    public Optional<PitPlayer> getPitPlayer(UUID uuid) {
        try (PreparedStatement statement = getConnection().prepareStatement(
            format("""
                    SELECT `kills`, `streaks`, `bestStreaks`, `deaths`, `rating`, `bestRating`, `xp`, `preferences`
                    FROM `%players_table%`
                    WHERE `uuid`=?"""))) {

            statement.setString(1, uuid.toString());

            final ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                final String preferences = new String(resultSet.getBytes("preferences"), StandardCharsets.UTF_8);
                return Optional.of(new PitPlayer(Bukkit.getPlayer(uuid),
                    resultSet.getLong("kills"),
                    resultSet.getLong("streaks"),
                    resultSet.getLong("bestStreaks"),
                    resultSet.getLong("deaths"),
                    resultSet.getDouble("rating"),
                    resultSet.getDouble("bestRating"),
                    resultSet.getDouble("xp"),
                    Optional.of(plugin.getGson().fromJson(preferences, Preferences.class))
                ));
            }
        } catch (SQLException e) {
            Bukkit.getLogger().log(Level.SEVERE,
                "Failed to fetch a player from uuid from the database", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<PitPreferences> getPitPreferences() {
        try (PreparedStatement statement = getConnection().prepareStatement(format("""
                SELECT `preferences`
                FROM `%pit_preferences%`
                """))) {
            final ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                final String preferences = new String(resultSet.getBytes("preferences"), StandardCharsets.UTF_8);
                return Optional.of(plugin.getGson().fromJson(preferences, PitPreferences.class));
            }
        } catch (SQLException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to fetch user data from table by UUID", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<OfflinePitPlayer> getOfflinePitPlayer(UUID uuid) {
        try (PreparedStatement statement = getConnection().prepareStatement(
            format("""
                    SELECT `kills`, `streaks`, `bestStreaks`, `deaths`, `rating`, `bestRating`, `xp`
                    FROM `%players_table%`
                    WHERE `uuid`=?"""))) {

            statement.setString(1, uuid.toString());

            final ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(new OfflinePitPlayer(uuid,
                    resultSet.getLong("kills"),
                    resultSet.getLong("streaks"),
                    resultSet.getLong("bestStreaks"),
                    resultSet.getLong("deaths"),
                    resultSet.getDouble("rating"),
                    resultSet.getDouble("bestRating"),
                    resultSet.getDouble("xp")
                ));
            }
        } catch (SQLException e) {
            Bukkit.getLogger().log(Level.SEVERE,
                "Failed to fetch a player from uuid from the database", e);
        }
        return Optional.empty();
    }

    @Override
    public CompletableFuture<Optional<Integer>> getPlayerRanking(PitPlayer player, RankType type) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String test = """
                    SELECT uuid, rank
                    FROM(SELECT `uuid`,
                    RANK()
                    OVER(ORDER BY %type% DESC)
                    AS rank FROM `%players_table%`)
                    WHERE `uuid`=?;
                    """;
                try (PreparedStatement statement = getConnection().prepareStatement(
                    format(test.replaceAll("%type%", type.name().toLowerCase())))) {
                    statement.setString(1, player.getUniqueId().toString());

                    final ResultSet resultSet = statement.executeQuery();
                    if (resultSet.next()) {
                        return Optional.of(resultSet.getInt("rank"));
                    }
                }
            } catch (SQLException e) {
                Bukkit.getLogger().log(Level.SEVERE,
                    "Failed to fetch a player from uuid from the database", e);
            }
            return Optional.empty();
        });
    }

    @Override
    public void createPitPlayer(Player player) {
        // Insert new player data into the database
        try {
            try (PreparedStatement statement = getConnection().prepareStatement(
                format("""
                                    INSERT INTO `%players_table%` (`uuid`,`username`,`preferences`)
                                    VALUES (?,?,?);"""))) {

                statement.setString(1, player.getUniqueId().toString());
                statement.setString(2, player.getName());
                statement.setBytes(3, plugin.getGson().toJson(Preferences.getDefaults()).getBytes(StandardCharsets.UTF_8));
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            Bukkit.getLogger().log(Level.SEVERE,
                "Failed to insert a player into the database", e);
        }
    }

    @Override
    public void createPitPreferences(PitPreferences pitPreferences) {
        try {
            try (PreparedStatement statement = getConnection().prepareStatement(
                format("""
                                    INSERT INTO `%pit_preferences%` (`preferences`)
                                    VALUES (?);"""))) {

                statement.setBytes(1, plugin.getGson().toJson(pitPreferences).getBytes(StandardCharsets.UTF_8));
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            Bukkit.getLogger().log(Level.SEVERE,
                "Failed to insert a player into the database", e);
        }
    }

    @Override
    public void updateUserData(PitPlayer player) {
        try {
            try (PreparedStatement statement = getConnection().prepareStatement(
                format("""
                    UPDATE `%players_table%`
                    SET `kills`=?, `streaks`=?, `bestStreaks`=?, `deaths`=?, `rating`=?, `bestRating`=?, `xp`=?, `preferences`=?
                    WHERE `uuid`=?"""))) {

                statement.setLong(1, player.getKills());
                statement.setLong(2, player.getStreaks());
                statement.setLong(3, player.getBestStreaks());
                statement.setLong(4, player.getDeaths());
                statement.setDouble(5, player.getRating());
                statement.setDouble(6, player.getBestRating());
                statement.setDouble(7, player.getXp());
                statement.setBytes(8, plugin.getGson().toJson(player.getPreferences().get()).getBytes(StandardCharsets.UTF_8));
                statement.setString(9, player.getUniqueId().toString());
                statement.executeUpdate();
            }

        } catch (SQLException e) {
            Bukkit.getLogger().log(Level.SEVERE,
                "Failed to update user data for " + player.getName() + " on the database", e);
        }
    }

    @Override
    public void updateUserData(OfflinePitPlayer player) {
        try {
            try (PreparedStatement statement = getConnection().prepareStatement(
                format("""
                    UPDATE `%players_table%`
                    SET `kills`=?, `streaks`=?, `deaths`=?, `rating`=?, `xp`=?
                    WHERE `uuid`=?"""))) {

                statement.setLong(1, player.getKills());
                statement.setLong(2, player.getStreaks());
                statement.setLong(3, player.getDeaths());
                statement.setDouble(4, player.getRating());
                statement.setDouble(5, player.getXp());
                statement.setString(6, player.getUniqueId().toString());
                statement.executeUpdate();
            }

        } catch (SQLException e) {
            Bukkit.getLogger().log(Level.SEVERE,
                "Failed to update user data for " + player.getUniqueId().toString() + " on the database", e);
        }
    }

    @Override
    public void updatePitPreferences(PitPreferences pitPreferences) {
        try (PreparedStatement statement = getConnection().prepareStatement(format("""
                UPDATE `%pit_preferences%`
                SET `preferences` = ?
                """))) {
            statement.setBytes(1, plugin.getGson().toJson(pitPreferences).getBytes(StandardCharsets.UTF_8));
            statement.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to update preferences in table", e);
        }
    }

    @Override
    public void deletePlayerData() {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(format("""
                    DELETE FROM `%players_table%`
                    """))) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to delete playerData from table", e);
        }
    }

    @Override
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
