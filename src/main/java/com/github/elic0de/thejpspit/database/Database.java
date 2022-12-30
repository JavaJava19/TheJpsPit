package com.github.elic0de.thejpspit.database;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.player.PitPlayer;
import org.bukkit.entity.Player;

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
                .replaceAll("%players_table%", playerTableName);
    }

    protected Database(TheJpsPit implementor) {
        this.plugin = implementor;
        this.playerTableName = "player";
        this.logger = implementor.getLogger();
    }

    public abstract boolean initialize();

    public abstract CompletableFuture<Void> runScript(InputStream inputStream, Map<String, String> replacements);

    public abstract CompletableFuture<Void> ensureUser(PitPlayer pitPlayer);

    public abstract CompletableFuture<Optional<PitPlayer>> getPitPlayer(Player player);

    public abstract void terminate();


}
