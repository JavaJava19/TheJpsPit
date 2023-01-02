package com.github.elic0de.thejpspit.player;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.database.Database;
import com.github.elic0de.thejpspit.util.ShowHealth;
import fr.mrmicky.fastboard.FastBoard;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.UUID;
import java.util.stream.Stream;

public class PitPlayer {

    private final Player player;
    private long kills;
    private long deaths;
    private double rating;
    private double xp;
    private FastBoard board;

    private final ItemStack[] INVENTORY = {
            new ItemStack(Material.IRON_SWORD),
            new ItemStack(Material.BOW),
            new ItemStack(Material.ARROW, 32),
            new ItemStack(Material.GOLDEN_APPLE),
            new ItemStack(Material.NETHER_STAR)
    };

    private final ItemStack[] ARMOR = {
            new ItemStack(Material.CHAINMAIL_BOOTS),
            new ItemStack(Material.CHAINMAIL_LEGGINGS),
            new ItemStack(Material.IRON_CHESTPLATE)
    };

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
        this.board.updateTitle(ChatColor.translateAlternateColorCodes('&', "&eTHE JPS PIT"));
    }

    public void addItem() {
        final PlayerInventory inventory = player.getInventory();

        inventory.setArmorContents(ARMOR);

        for (ItemStack item : INVENTORY) {
            if (inventory.contains(item)) continue;
            inventory.addItem(item);
        }
        player.updateInventory();
    }

    public void showHealth(PitPlayer targetPit) {
        ShowHealth.showHealth(this, targetPit);
    }

    public void sendMessage(String message) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public void sendStatus() {
        final TheJpsPit pit = TheJpsPit.getInstance();
        Stream.of(
                "+ -----< %playerName% >----- +",
                        "Kills >> &e%kills% (#%kills_ranking%)",
                        "Deaths >> &c%deaths% (#%deaths_ranking%)",
                        "Rating >> &a%rating% (#%rating_ranking%)"
        ).map(s ->
                s.replaceAll("%playerName%", getName())
                        .replaceAll("%kills%",
                                kills + "")
                        .replaceAll("%deaths%",
                                deaths + "")
                        .replaceAll("%rating%",
                                rating + "%")
                        .replaceAll("%kills_ranking%",
                                pit.getDatabase().getPlayerRanking(this, Database.RankType.KILLS).join().orElse(0) + "")
                        .replaceAll("%deaths_ranking%",
                                pit.getDatabase().getPlayerRanking(this, Database.RankType.DEATHS).join().orElse(0) + "")
                        .replaceAll("%rating_ranking%",
                                pit.getDatabase().getPlayerRanking(this, Database.RankType.RATING).join().orElse(0) + "")
        ).forEach(string -> sendMessage(string));
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

    public void increaseXP() {
        this.xp++;
    }

    public void increaseHealth() {
        player.setHealth(Math.min(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(), player.getHealth() + 1));
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
