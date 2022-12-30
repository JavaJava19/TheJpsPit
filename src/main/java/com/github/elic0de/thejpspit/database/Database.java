package com.github.elic0de.thejpspit.database;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.player.PitPlayer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public abstract class Database {

    protected final String playerTableName;

    protected final TheJpsPit plugin;

    private final Logger logger;

    protected Logger getLogger() {
        return logger;
    }

    protected final String[] getSchemaStatements(String schemaFileName) throws IOException {
        return formatStatementTables(
                new String(Objects.requireNonNull(plugin.getResource(schemaFileName)).readAllBytes(),
                        StandardCharsets.UTF_8))
                .split(";");
    }

    protected final String formatStatementTables(String sql) {
        return sql
                .replaceAll("%positions_table%", positionsTableName)
                .replaceAll("%players_table%", playerTableName)
                .replaceAll("%teleports_table%", teleportsTableName)
                .replaceAll("%saved_positions_table%", savedPositionsTableName)
                .replaceAll("%homes_table%", homesTableName)
                .replaceAll("%warps_table%", warpsTableName);
    }

    protected Database(TheJpsPit implementor) {
        this.plugin = implementor;
        this.playerTableName = implementor.getSettings().getTableName(Settings.TableName.PLAYER_DATA);
        this.logger = implementor.g();
    }

    public abstract boolean initialize();

    public abstract CompletableFuture<Void> runScript(InputStream inputStream, Map<String, String> replacements);
    public abstract CompletableFuture<Optional<PitPlayer>> getPitPlayer(UUID uuid);

    public abstract void terminate();


}
