package com.github.elic0de.thejpspit.player;

import fr.mrmicky.fastboard.FastBoard;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class OfflinePitPlayer {

    private final UUID uuid;
    private long kills;

    private long streaks;
    private long bestStreaks;
    private long deaths;
    private double rating;
    private double bestRating;
    private final double xp;

    public OfflinePitPlayer(UUID uuid,long kills, long streaks, long bestStreaks, long deaths, double rating, double bestRating, double xp) {
        this.uuid = uuid;
        this.kills = kills;
        this.streaks = streaks;
        this.bestStreaks = bestStreaks;
        this.deaths = deaths;
        this.rating = rating;
        this.bestRating = bestRating;
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

    public long getBestStreaks() {
        return bestStreaks;
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

    public double getBestRating() {
        return bestRating;
    }

    public void setRating(double rating) {
        this.rating = rating;
        if (bestRating > rating) {
            this.bestRating = rating;
        }
    }

    public void increaseKills() {
        this.kills++;
    }

    public void increaseStreaks() {
        this.streaks ++;
        if (bestStreaks > streaks) {
            this.bestStreaks = streaks;
        }
    }
    public void increaseDeaths() {
        this.deaths++;
    }

    public void resetStreaks() {
        streaks = 0;
    }

}
