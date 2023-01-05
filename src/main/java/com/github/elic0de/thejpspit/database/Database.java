package com.github.elic0de.thejpspit.database;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.player.PitPlayer;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import org.bukkit.entity.Player;

public abstract class Database {

    protected final String playerTableName;

    protected final TheJpsPit plugin;

    private final Logger logger;

    protected Database(TheJpsPit implementor) {
        this.plugin = implementor;
        this.playerTableName = "player";
        this.logger = implementor.getLogger();
    }

    protected Logger getLogger() {
        return logger;
    }

    protected final String[] getSchemaStatements() throws IOException {
        return formatStatementTables(
            new String(
                Objects.requireNonNull(plugin.getResource("database/sqlite_schema.sql"))
                    .readAllBytes(),
                StandardCharsets.UTF_8))
            .split(";");
    }

    protected final String formatStatementTables(String sql) {
        return sql
            .replaceAll("%players_table%", playerTableName);
    }

    public abstract boolean initialize();

    public abstract CompletableFuture<Void> runScript(InputStream inputStream,
        Map<String, String> replacements);

    public abstract CompletableFuture<Void> ensureUser(Player Player);

    public abstract CompletableFuture<Optional<PitPlayer>> getPitPlayer(Player player);

    public abstract CompletableFuture<Optional<Integer>> getPlayerRanking(PitPlayer player,
        RankType type);

    public abstract CompletableFuture<Void> updateUserData(PitPlayer player);

    public abstract void terminate();

    public enum RankType {
        KILLS,
        DEATHS,
        RATING
    }

}
