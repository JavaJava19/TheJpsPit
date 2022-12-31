package com.github.elic0de.thejpspit.player;

import com.github.elic0de.thejpspit.util.ShowHealth;
import fr.mrmicky.fastboard.FastBoard;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemDamageEvent;

import java.util.UUID;

public class PitPlayer {

    private final Player player;
    private long kills;
    private long deaths;
    private double rating;
    private double xp;

    private FastBoard board;

    public PitPlayer(Player player) {
        this.player = player;
        this.kills = 0;
        this.deaths = 0;
        this.rating = 0;
        this.xp = 0;
    }

    public PitPlayer(Player player, long kills, long deaths, double rating, double xp) {
        this.player = player;
        this.kills = kills;
        this.deaths = deaths;
        this.rating = rating;
        this.xp = xp;
        this.board = new FastBoard(player);
        this.board.updateTitle("THE JPS PIT");
        this.board.updateLines("");
    }

    public static PitPlayer adapt(Player player) {
        return new PitPlayer(player);
    }

    public void showHealth(PitPlayer targetPit) {
        ShowHealth.showHealth(this, targetPit);
    }

    public void sendMessage(String message) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public Player getPlayer() {
        return player;
    }

    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    public String getName() {
        return player.getName();
    }

    public PitPlayer getKiller() {
        return adapt(player.getKiller());
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

    public double getXp() {
        return xp;
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

    public void increaseHealth() {
        player.setHealth(Math.min(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(), player.getHealth() + 1));
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
