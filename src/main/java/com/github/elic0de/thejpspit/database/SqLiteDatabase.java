package com.github.elic0de.thejpspit.database;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.player.PitPlayer;
import org.bukkit.entity.Player;
import org.sqlite.SQLiteConfig;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class SqLiteDatabase extends Database {

    private final File databaseFile;
    private static final String DATABASE_FILE_NAME = "TheJpsPitData.db";
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
            connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile.getAbsolutePath(), config.toProperties());
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "An exception occurred creating the database file", e);
        } catch (SQLException e) {
            getLogger().log(Level.SEVERE, "An SQL exception occurred initializing the SQLite database", e);
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
                getLogger().log(Level.SEVERE, "An error occurred creating tables on the SQLite database: ", e);
            }
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "An unhandled exception occurred during database setup!", e);
        }
        return false;
    }

    @Override
    public CompletableFuture<Void> runScript(InputStream inputStream, Map<String, String> replacements) {
        return CompletableFuture.runAsync(() -> {
            try {
                final String[] scriptString;
                scriptString = new String[]{new String(inputStream.readAllBytes(), StandardCharsets.UTF_8)};
                replacements.forEach((key, value) -> scriptString[0] = scriptString[0].replaceAll(key, value));
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
                getLogger().log(Level.SEVERE, "An exception occurred running script on the SQLite database", e);
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> ensureUser(PitPlayer onlineUser) {
        return CompletableFuture.runAsync(() -> getPitPlayer(onlineUser.getPlayer()).thenAccept(optionalUser ->
                optionalUser.ifPresentOrElse(existingUser -> {
                            if (!existingUser.getName().equals(onlineUser.getName())) {
                                // Update a player's name if it has changed in the database
                                try {
                                    try (PreparedStatement statement = getConnection().prepareStatement(formatStatementTables("""
                                            UPDATE `%players_table%`
                                            SET `username`=?
                                            WHERE `uuid`=?"""))) {

                                        statement.setString(1, onlineUser.getName());
                                        statement.setString(2, existingUser.getUniqueId().toString());
                                        statement.executeUpdate();
                                    }
                                    getLogger().log(Level.INFO, "Updated " + onlineUser.getName() + "'s name in the database (" + existingUser.getName() + " -> " + onlineUser.getName() + ")");
                                } catch (SQLException e) {
                                    getLogger().log(Level.SEVERE, "Failed to update a player's name on the database", e);
                                }
                            }
                        },
                        () -> {
                            // Insert new player data into the database
                            try {
                                try (PreparedStatement statement = getConnection().prepareStatement(formatStatementTables("""
                                        INSERT INTO `%players_table%` (`uuid`,`username`)
                                        VALUES (?,?);"""))) {

                                    statement.setString(1, onlineUser.getUniqueId().toString());
                                    statement.setString(2, onlineUser.getName());
                                    statement.executeUpdate();
                                }
                            } catch (SQLException e) {
                                getLogger().log(Level.SEVERE, "Failed to insert a player into the database", e);
                            }
                        })));
    }

    @Override
    public CompletableFuture<Optional<PitPlayer>> getPitPlayer(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                try (PreparedStatement statement = getConnection().prepareStatement(formatStatementTables("""
                        SELECT `kills`, `deaths`, `rating`, `xp`
                        FROM `%players_table%`
                        WHERE `uuid`=?"""))) {

                    statement.setString(1, player.getUniqueId().toString());

                    final ResultSet resultSet = statement.executeQuery();
                    if (resultSet.next()) {
                        return Optional.of(new PitPlayer(player,
                                resultSet.getLong("kills"),
                                resultSet.getLong("deaths"),
                                resultSet.getDouble("rating"),
                                resultSet.getDouble("xp")
                        ));
                    }
                }
            } catch (SQLException e) {
                getLogger().log(Level.SEVERE, "Failed to fetch a player from uuid from the database", e);
            }
            return Optional.empty();
        });
    }

    @Override
    public CompletableFuture<Void> updateUserData(PitPlayer player) {
        return CompletableFuture.runAsync(() -> {
            try {
                try (PreparedStatement statement = getConnection().prepareStatement(formatStatementTables("""
                        UPDATE `%players_table%`
                        SET `kills`=?, `deaths`=?, `rating`=?, `xp`=?
                        WHERE `uuid`=?"""))) {

                    statement.setLong(1, player.getKills());
                    statement.setLong(2, player.getDeaths());
                    statement.setDouble(3, player.getRating());
                    statement.setDouble(4, player.getXp());
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                getLogger().log(Level.SEVERE, "Failed to update user data for " + player.getName() + " on the database", e);
            }
        });
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
