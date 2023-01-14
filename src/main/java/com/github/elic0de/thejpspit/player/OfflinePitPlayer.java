package com.github.elic0de.thejpspit.player;

import fr.mrmicky.fastboard.FastBoard;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class OfflinePitPlayer {

    private final UUID uuid;
    private long kills;

    private long streaks;
    private long deaths;
    private double rating;
    private double xp;

    public OfflinePitPlayer(UUID uuid,long kills, long streaks, long deaths, double rating, double xp) {
        this.uuid = uuid;
        this.kills = kills;
        this.streaks = streaks;
        this.deaths = deaths;
        this.rating = rating;
        this.xp = xp;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public long getKills() {
        return kills;
    }

    public long getStreaks() {
        return streaks;
    }

    public long getDeaths() {
        return deaths;
    }

    public double getXp() {
        return xp;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void increaseKills() {
        this.kills++;
    }

    public void increaseStreaks() {
        this.streaks ++;
    }
    public void increaseDeaths() {
        this.deaths++;
    }

    public void resetStreaks() {
        streaks = 0;
    }

}
