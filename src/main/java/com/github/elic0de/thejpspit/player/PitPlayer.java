package com.github.elic0de.thejpspit.player;

import fr.mrmicky.fastboard.FastBoard;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PitPlayer {

    private final Player player;
    private long kills;
    private long deaths;
    private double rating;
    private double xp;

    private final FastBoard board;

    public PitPlayer(Player player) {
        this.player = player;
        this.kills = 0;
        this.deaths = 0;
        this.rating = 0;
        this.xp = 0;
        this.board = new FastBoard(player);
        this.board.updateTitle("THE JPS PIT");
    }

    public PitPlayer(Player player, long kills, long deaths, double rating, double xp) {
        this.player = player;
        this.kills = kills;
        this.deaths = deaths;
        this.rating = rating;
        this.xp = xp;
        this.board = new FastBoard(player);
        this.board.updateTitle("THE JPS PIT");
    }

    public static PitPlayer adapt(Player player) {
        return new PitPlayer(player);
    }

    public void showHealth() {
        double maxHealth = player.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        double health = player.getHealth() - player.getLastDamage();

        if (health < 0.0 || player.isDead()) health = 0.0;

        StringBuilder style = new StringBuilder();
        int left = 10;
        double heart = maxHealth / left;
        double halfHeart = heart ;
        double tempHealth = health;

        if (maxHealth != health && health >= 0 && !player.isDead()) {
            for (int i = 0; i < maxHealth; i++) {
                if (tempHealth - heart > 0) {
                    tempHealth = tempHealth - heart;

                    style.append("&4\u2764");
                    left--;
                } else {
                    break;
                }
            }

            if (tempHealth > halfHeart) {
                style.append("&4\u2764");
                left--;
            } else if (tempHealth > 0 && tempHealth <= halfHeart) {
                style.append("&c\u2764");
                left--;
            }
        }

        if (maxHealth != health) {
            style.append("&7\u2764".repeat(Math.max(0, left)));
        } else {
            style.append("&4\u2764".repeat(Math.max(0, left)));
        }

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', style.toString())).create());
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
