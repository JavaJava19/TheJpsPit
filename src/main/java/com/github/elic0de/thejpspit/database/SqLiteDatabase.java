package com.github.elic0de.thejpspit.database;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.player.PitPlayer;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Optional;
import java.util.UUID;
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
                final String[] databaseSchema = getSchemaStatements("database/sqlite_schema.sql");
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
    public CompletableFuture<Void> runScript(@NotNull InputStream inputStream, @NotNull Map<String, String> replacements) {
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
    public CompletableFuture<Optional<PitPlayer>> getPitUser(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                try (PreparedStatement statement = getConnection().prepareStatement(formatStatementTables("""
                        SELECT `uuid`, `username`, `home_slots`, `ignoring_requests`, `rtp_cooldown`
                        FROM `%players_table%`
                        WHERE `uuid`=?"""))) {

                    statement.setString(1, uuid.toString());

                    final ResultSet resultSet = statement.executeQuery();
                    if (resultSet.next()) {
                        return Optional.of(new UserData(
                                new User(UUID.fromString(resultSet.getString("uuid")),
                                        resultSet.getString("username")),
                                resultSet.getInt("home_slots"),
                                resultSet.getBoolean("ignoring_requests"),
                                resultSet.getTimestamp("rtp_cooldown").toInstant()));
                    }
                }
            } catch (SQLException e) {
                getLogger().log(Level.SEVERE, "Failed to fetch a player from uuid from the database", e);
            }
            return Optional.empty();
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
