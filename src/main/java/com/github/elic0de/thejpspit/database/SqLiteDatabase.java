package com.github.elic0de.thejpspit.database;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.player.OfflinePitPlayer;
import com.github.elic0de.thejpspit.player.PitPlayer;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.sqlite.SQLiteConfig;

public class SqLiteDatabase extends Database {

    private static final String DATABASE_FILE_NAME = "TheJpsPitData.db";
    private final File databaseFile;
    private Connection connection;

    public SqLiteDatabase(TheJpsPit implementor) {
        super(implementor);
        this.databaseFile = new File(implementor.getDataFolder(), DATABASE_FILE_NAME);
    }

    private Connection getConnection() throws SQLException {
        if (connection == null) {
            setConnection();
        } else if (connection.isClosed()) {
            setConnection();
        }
        return connection;
    }

    private void setConnection() {
        try {
            // Ensure that the database file exists
            if (databaseFile.createNewFile()) {
                getLogger().log(Level.INFO, "Created the SQLite database file");
            }

            // Specify use of the JDBC SQLite driver
            Class.forName("org.sqlite.JDBC");

            // Set SQLite database properties
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            config.setEncoding(SQLiteConfig.Encoding.UTF8);
            config.setSynchronous(SQLiteConfig.SynchronousMode.FULL);

            // Establish the connection
            connection = DriverManager.getConnection(
                "jdbc:sqlite:" + databaseFile.getAbsolutePath(),
                config.toProperties());
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "An exception occurred creating the database file", e);
        } catch (SQLException e) {
            getLogger().log(Level.SEVERE,
                "An SQL exception occurred initializing the SQLite database",
                e);
        } catch (ClassNotFoundException e) {
            getLogger().log(Level.SEVERE, "Failed to load the necessary SQLite driver", e);
        }
    }

    @Override
    public boolean initialize() {
        try {
            // Set up the connection
            setConnection();

            // Prepare database schema; make tables if they don't exist
            try {
                // Load database schema CREATE statements from schema file
                final String[] databaseSchema = getSchemaStatements();
                try (Statement statement = getConnection().createStatement()) {
                    for (String tableCreationStatement : databaseSchema) {
                        statement.execute(tableCreationStatement);
                    }
                }
                return true;
            } catch (SQLException | IOException e) {
                getLogger().log(Level.SEVERE,
                    "An error occurred creating tables on the SQLite database: ",
                    e);
            }
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "An unhandled exception occurred during database setup!",
                e);
        }
        return false;
    }

    @Override
    public CompletableFuture<Void> runScript(InputStream inputStream,
        Map<String, String> replacements) {
        return CompletableFuture.runAsync(() -> {
            try {
                final String[] scriptString;
                scriptString = new String[]{
                    new String(inputStream.readAllBytes(), StandardCharsets.UTF_8)};
                replacements.forEach(
                    (key, value) -> scriptString[0] = scriptString[0].replaceAll(key, value));
                final boolean autoCommit = getConnection().getAutoCommit();

                // Execute batched SQLite script
                getConnection().setAutoCommit(false);
                try (Statement statement = getConnection().createStatement()) {
                    for (String statementString : scriptString[0].split(";")) {
                        statement.addBatch(statementString);
                    }
                    statement.executeBatch();
                }
                getConnection().setAutoCommit(autoCommit);
            } catch (IOException | SQLException e) {
                getLogger().log(Level.SEVERE,
                    "An exception occurred running script on the SQLite database",
                    e);
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public Optional<PitPlayer> getPitPlayer(Player player) {
        return getPitPlayer(player.getUniqueId());
    }

    @Override
    public Optional<PitPlayer> getPitPlayer(UUID uuid) {
        try (PreparedStatement statement = getConnection().prepareStatement(
            formatStatementTables("""
                    SELECT `kills`, `streaks`, `deaths`, `rating`, `xp`
                    FROM `%players_table%`
                    WHERE `uuid`=?"""))) {

            statement.setString(1, uuid.toString());

            final ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(new PitPlayer(Bukkit.getPlayer(uuid),
                    resultSet.getLong("kills"),
                    resultSet.getLong("streaks"),
                    resultSet.getLong("deaths"),
                    resultSet.getDouble("rating"),
                    resultSet.getDouble("xp")
                ));
            }
        } catch (SQLException e) {
            getLogger().log(Level.SEVERE,
                "Failed to fetch a player from uuid from the database", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<OfflinePitPlayer> getOfflinePitPlayer(UUID uuid) {
        try (PreparedStatement statement = getConnection().prepareStatement(
            formatStatementTables("""
                    SELECT `kills`, `streaks`, `deaths`, `rating`, `xp`
                    FROM `%players_table%`
                    WHERE `uuid`=?"""))) {

            statement.setString(1, uuid.toString());

            final ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(new OfflinePitPlayer(uuid,
                    resultSet.getLong("kills"),
                    resultSet.getLong("streaks"),
                    resultSet.getLong("deaths"),
                    resultSet.getDouble("rating"),
                    resultSet.getDouble("xp")
                ));
            }
        } catch (SQLException e) {
            getLogger().log(Level.SEVERE,
                "Failed to fetch a player from uuid from the database", e);
        }
        return Optional.empty();
    }

    @Override
    public CompletableFuture<Optional<Integer>> getPlayerRanking(PitPlayer player, RankType type) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                try (PreparedStatement statement = getConnection().prepareStatement(
                    formatStatementTables("""
                        SELECT RANK() OVER(ORDER BY ? DESC)
                        AS rank
                        FROM `%players_table%`
                        WHERE `uuid`=?"""))) {

                    statement.setString(1, type.name().toLowerCase(Locale.ROOT));
                    statement.setString(2, player.getUniqueId().toString());

                    final ResultSet resultSet = statement.executeQuery();
                    if (resultSet.next()) {
                        return Optional.of(resultSet.getInt("rank"));
                    }
                }
            } catch (SQLException e) {
                getLogger().log(Level.SEVERE,
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
                formatStatementTables("""
                                    INSERT INTO `%players_table%` (`uuid`,`username`)
                                    VALUES (?,?);"""))) {

                statement.setString(1, player.getUniqueId().toString());
                statement.setString(2, player.getName());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            getLogger().log(Level.SEVERE,
                "Failed to insert a player into the database", e);
        }
    }

    @Override
    public void updateUserData(PitPlayer player) {
        try {
            try (PreparedStatement statement = getConnection().prepareStatement(
                formatStatementTables("""
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
            getLogger().log(Level.SEVERE,
                "Failed to update user data for " + player.getName() + " on the database", e);
        }
    }

    @Override
    public void updateUserData(OfflinePitPlayer player) {
        try {
            try (PreparedStatement statement = getConnection().prepareStatement(
                formatStatementTables("""
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
            getLogger().log(Level.SEVERE,
                "Failed to update user data for " + player.getUniqueId().toString() + " on the database", e);
        }
    }

    @Override
    public void deletePlayerData() {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(formatStatementTables("""
                    DELETE FROM `%players_table%`
                    """))) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            getLogger().log(Level.SEVERE, "Failed to delete playerData from table", e);
        }
    }

    @Override
    public void terminate() {
        try {
            if (connection != null) {
                if (!connection.isClosed()) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            getLogger().log(Level.WARNING, "Failed to properly close the SQLite connection");
        }
    }
}
