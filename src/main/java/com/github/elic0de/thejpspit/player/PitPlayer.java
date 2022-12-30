package com.github.elic0de.thejpspit.player;

import fr.mrmicky.fastboard.FastBoard;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PitPlayer {

    private final Player player;
    private long kills;
    private long deaths;
    private double rating;

    private final FastBoard board;

    public PitPlayer(Player player) {
        this.player = player;
        this.kills = 0;
        this.deaths = 0;
        this.rating = 0;
    }

    public PitPlayer(Player player, long kills, long deaths, double rating) {
        this.player = player;
        this.kills = kills;
        this.deaths = deaths;
        this.rating = rating;
    }

    public void sendMessage(String message) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public Player getPlayer() {
        return player;
    }

    public PitPlayer getKiller() {
        return PitPlayerManager.getPitPlayer(player.getKiller());
    }

    public long getKills() {
        return kills;
    }

    public long getDeaths() {
        return deaths;
    }

    public double getRating() {
        return rating;
    }

    public FastBoard getBoard() {
        return board;
    }

    public void increaseKills() {
        this.kills++;
    }

    public void increaseDeaths() {
        this.deaths++;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
