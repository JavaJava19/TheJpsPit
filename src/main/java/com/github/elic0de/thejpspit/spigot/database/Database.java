package com.github.elic0de.thejpspit.spigot.database;

import com.github.elic0de.thejpspit.spigot.TheJpsPit;
import com.github.elic0de.thejpspit.spigot.config.PitPreferences;
import com.github.elic0de.thejpspit.spigot.player.OfflinePitPlayer;
import com.github.elic0de.thejpspit.spigot.player.PitPlayer;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import org.bukkit.entity.Player;

public abstract class Database {

    protected final String playerTableName;
    protected final String preferencesTableName;

    protected final TheJpsPit plugin;

    private final Logger logger;

    protected Database(TheJpsPit implementor) {
        this.plugin = implementor;
        this.playerTableName = "player";
        this.preferencesTableName = "preferences";
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
            .replaceAll("%pit_preferences%", preferencesTableName)
            .replaceAll("%players_table%", playerTableName);
    }

    public abstract boolean initialize();

    public abstract CompletableFuture<Void> runScript(InputStream inputStream,
        Map<String, String> replacements);

    public abstract void createPitPlayer(Player Player);

    public abstract void createPitPreferences(PitPreferences preferences);

    public abstract Optional<PitPlayer> getPitPlayer(Player player);

    public abstract Optional<PitPlayer> getPitPlayer(UUID uuid);

    public abstract Optional<PitPreferences> getPitPreferences();

    public abstract Optional<OfflinePitPlayer> getOfflinePitPlayer(UUID uuid);

    public abstract CompletableFuture<Optional<Integer>> getPlayerRanking(PitPlayer player,
        RankType type);

    public abstract void updateUserData(PitPlayer player);

    public abstract void updateUserData(OfflinePitPlayer player);

    public abstract void updatePitPreferences(PitPreferences pitPreferences);

    public abstract void deletePlayerData();

    public abstract void terminate();

    public enum RankType {
        KILLS,
        DEATHS,
        RATING
    }

}
