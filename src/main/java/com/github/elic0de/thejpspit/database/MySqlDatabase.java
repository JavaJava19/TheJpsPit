package com.github.elic0de.thejpspit.database;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.config.PitPreferences;
import com.github.elic0de.thejpspit.config.Settings;
import com.github.elic0de.thejpspit.player.OfflinePitPlayer;
import com.github.elic0de.thejpspit.player.PitPlayer;
import com.zaxxer.hikari.HikariDataSource;
import java.io.InputStream;
import java.sql.Connection;
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
    public void createPitPlayer(Player Player) {

    }

    @Override
    public void createPitPreferences(PitPreferences preferences) {

    }

    @Override
    public Optional<PitPlayer> getPitPlayer(Player player) {
        return Optional.empty();
    }

    @Override
    public Optional<PitPlayer> getPitPlayer(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public Optional<PitPreferences> getPitPreferences() {
        return Optional.empty();
    }

    @Override
    public Optional<OfflinePitPlayer> getOfflinePitPlayer(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public CompletableFuture<Optional<Integer>> getPlayerRanking(PitPlayer player, RankType type) {
        return null;
    }

    @Override
    public void updateUserData(PitPlayer player) {

    }

    @Override
    public void updateUserData(OfflinePitPlayer player) {

    }

    @Override
    public void updatePitPreferences(PitPreferences pitPreferences) {

    }

    @Override
    public void deletePlayerData() {

    }

    @Override
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
