package com.github.elic0de.thejpspit.database;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.config.PitPreferences;
import com.github.elic0de.thejpspit.player.OfflinePitPlayer;
import com.github.elic0de.thejpspit.player.PitPlayer;
import com.github.elic0de.thejpspit.player.Preferences;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
                Bukkit.getLogger().log(Level.INFO, "Created the SQLite database file");
            }

            // Specify use of the JDBC SQLite driver
            Class.forName("org.sqlite.JDBC");

            // Set SQLite database properties
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            config.setEncoding(SQLiteConfig.Encoding.UTF8);
            config.setSynchronous(SQLiteConfig.SynchronousMode.FULL);

            // Establish the connection
            connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile.getAbsolutePath(), config.toProperties());
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "An exception occurred creating the database file", e);
        } catch (SQLException e) {
            Bukkit.getLogger().log(Level.SEVERE, "An SQL exception occurred initializing the SQLite database", e);
        } catch (ClassNotFoundException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to load the necessary SQLite driver", e);
        }
    }

    public SqLiteDatabase(TheJpsPit plugin) {
        super(plugin, "sqlite_schema.sql");
        this.databaseFile = new File(plugin.getDataFolder(), DATABASE_FILE_NAME);
    }



    @Override
    public void initialize() throws RuntimeException {
        // Establish connection
        this.setConnection();

        // Create tables
        try (Statement statement = getConnection().createStatement()) {
            for (String tableCreationStatement : getSchema()) {
                statement.execute(tableCreationStatement);
            }
            setLoaded(true);
        } catch (SQLException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to create SQLite database tables");
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
                    FROM `%user_data%`
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
                FROM `%pit_data%`
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
                    FROM `%user_data%`
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
    public Optional<Integer> getPlayerRanking(PitPlayer player, RankType type) {
        try {
            String test = """
                SELECT uuid, rank
                FROM(SELECT `uuid`,
                RANK()
                OVER(ORDER BY %type% DESC)
                AS rank FROM `%user_data%`)
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
    }

    @Override
    public void createPitPlayer(Player player) {
        // Insert new player data into the database
        try {
            try (PreparedStatement statement = getConnection().prepareStatement(
                format("""
                                    INSERT INTO `%user_data%` (`uuid`,`username`,`preferences`)
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
                                    INSERT INTO `%pit_data%` (`preferences`)
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
                    UPDATE `%user_data%`
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
                    UPDATE `%user_data%`
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
                UPDATE `%pit_data%`
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
                    DELETE FROM `%user_data%`
                    """))) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to delete playerData from table", e);
        }
    }

    @Override
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to close connection", e);
        }
    }
}
