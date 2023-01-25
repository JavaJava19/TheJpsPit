package com.github.elic0de.thejpspit.database;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.config.PitPreferences;
import com.github.elic0de.thejpspit.player.OfflinePitPlayer;
import com.github.elic0de.thejpspit.player.PitPlayer;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public abstract class Database {


    protected final TheJpsPit plugin;
    private final String schemaFile;
    private boolean loaded;

    protected Database(TheJpsPit plugin, String schemaFile) {
        this.plugin = plugin;
        this.schemaFile = "database/" + schemaFile;
    }

    protected final String[] getSchema() {
        try (InputStream schemaStream = Objects.requireNonNull(plugin.getResource(schemaFile))) {
            final String schema = new String(schemaStream.readAllBytes(), StandardCharsets.UTF_8);
            return format(schema).split(";");
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to load database schema", e);
        }
        return new String[0];
    }

    protected final String format( String statement) {
        final Pattern pattern = Pattern.compile("%(\\w+)%");
        final Matcher matcher = pattern.matcher(statement);
        final StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            final Table table = Table.match(matcher.group(1));
            matcher.appendReplacement(sb, plugin.getSettings().getTableName(table));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public abstract void initialize() throws RuntimeException;

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

    public abstract void close();

    public boolean hasLoaded() {
        return loaded;
    }

    protected void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public enum Type {
        MYSQL("MySQL"),
        SQLITE("SQLite");
        private final String displayName;

        Type(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum Table {
        USER_DATA("pit_users"),
        PIT_DATA("pit_data");

        private final String defaultName;

        Table(String defaultName) {
            this.defaultName = defaultName;
        }

        public static Database.Table match(String placeholder) throws IllegalArgumentException {
            return Table.valueOf(placeholder.toUpperCase());
        }

        public String getDefaultName() {
            return defaultName;
        }
    }

    public enum RankType {
        KILLS,
        DEATHS,
        RATING
    }
}
