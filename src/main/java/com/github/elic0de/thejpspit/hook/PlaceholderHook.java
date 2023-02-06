package com.github.elic0de.thejpspit.hook;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.database.Database.RankType;
import com.github.elic0de.thejpspit.player.PitPlayer;
import com.github.elic0de.thejpspit.player.PitPlayerManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlaceholderHook extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "thejpspit";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Elic0de";
    }

    @Override
    public @NotNull String getVersion() {
        return "2.0";
    }

    @Override
    public String onRequest(@Nullable OfflinePlayer offlinePlayer, @NotNull String params) {
        // Ensure the player is online
        if (offlinePlayer == null || !offlinePlayer.isOnline()) {
            return "Player offline";
        }

        // Return the requested placeholder
        final PitPlayer player = PitPlayerManager.getPitPlayer(offlinePlayer.getPlayer());
        return switch (params) {
            case "kill_ranking" -> String.valueOf(TheJpsPit.getInstance().getDatabase().getPlayerRanking(player, RankType.KILLS).orElse(0));
            case "kill" -> String.valueOf(player.getKills());
            case "level" -> String.valueOf(player.getLevel());
            default -> null;
        };
    }
}
