package com.github.elic0de.thejpspit.player;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.database.Database;
import com.github.elic0de.thejpspit.leveler.Levels;
import com.github.elic0de.thejpspit.util.ShowHealth;
import fr.mrmicky.fastboard.FastBoard;
import java.util.UUID;
import java.util.stream.Stream;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PitPlayer {

    private final Player player;

    private final String name;

    private final UUID uuid;
    private final ItemStack[] INVENTORY = {
        new ItemStack(Material.IRON_SWORD),
        new ItemStack(Material.BOW),
        new ItemStack(Material.ARROW, 32),
        new ItemStack(Material.GOLDEN_APPLE),
        new ItemStack(Material.NETHER_STAR)
    };
    private final ItemStack[] ARMOR = {
        new ItemStack(Material.CHAINMAIL_BOOTS),
        new ItemStack(Material.IRON_LEGGINGS),
        new ItemStack(Material.IRON_CHESTPLATE)
    };
    private long kills;

    private long streaks;
    private long bestStreaks;
    private long deaths;
    private double rating;
    private double bestRating;
    private double xp;
    private final FastBoard board;

    public PitPlayer(Player player) {
        this.player = player;
        this.name = player.getName();
        this.uuid = player.getUniqueId();
        this.kills = 0;
        this.streaks = 0;
        this.bestStreaks = 0;
        this.deaths = 0;
        this.rating = 0;
        this.bestRating = 0;
        this.xp = 0;
        this.board = new FastBoard(player);
        this.board.updateTitle(ChatColor.translateAlternateColorCodes('&', "&eTHE JPS PIT"));
    }

    public PitPlayer(Player player, long kills, long streaks, long bestStreaks, long deaths, double rating, double bestRating, double xp) {
        this.player = player;
        this.name = player.getName();
        this.uuid = player.getUniqueId();
        this.kills = kills;
        this.streaks = streaks;
        this.bestStreaks = bestStreaks;
        this.deaths = deaths;
        this.rating = rating;
        this.bestRating = bestRating;
        this.xp = xp;
        this.board = new FastBoard(player);
        this.board.updateTitle(ChatColor.translateAlternateColorCodes('&', "&eTHE JPS PIT"));
    }

    public void addItem() {
        final PlayerInventory inventory = player.getInventory();

        inventory.setArmorContents(ARMOR);
        inventory.remove(Material.ARROW);
        for (ItemStack item : INVENTORY) {
            if (inventory.contains(item)) {
                continue;
            }
            inventory.addItem(item);
        }
        player.updateInventory();
    }

    public void resetItem() {
        player.getInventory().clear();
        addItem();
    }

    public void addReward() {
        final PlayerInventory inventory = player.getInventory();

        inventory.addItem(new ItemStack(Material.GOLDEN_APPLE));
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
                    pit.getDatabase().getPlayerRanking(this, Database.RankType.KILLS).join()
                        .orElse(0)
                        + "")
                .replaceAll("%deaths_ranking%",
                    pit.getDatabase().getPlayerRanking(this, Database.RankType.DEATHS).join()
                        .orElse(0)
                        + "")
                .replaceAll("%rating_ranking%",
                    pit.getDatabase().getPlayerRanking(this, Database.RankType.RATING).join()
                        .orElse(0)
                        + "")
        ).forEach(this::sendMessage);
    }

    private void updateXpBar() {
        final float xp = Levels.getPlayerNeededXP(this);
        final int level = Levels.getPlayerLevel(this);
        /*player.setLevel(level);
        player.setExp(Math.abs(100 - xp) / 100);*/
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
        if (player.getKiller() == null) return null;
        return PitPlayerManager.getPitPlayer(player.getKiller());
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

    public double getRating() {
        return rating;
    }

    public double getBestRating() {
        return bestRating;
    }

    public void setRating(double rating) {
        this.rating = rating;
        if (bestRating < rating) {
            this.bestRating = rating;
        }
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

    public void increaseStreaks() {
        this.streaks ++;

        if (streaks % 5 == 0) TheJpsPit.getInstance().getGame().broadcast(player.getName() + "&aが連続で&c" + streaks + "&aキルしています！" );

        if (bestStreaks < streaks) {
            this.bestStreaks = streaks;
        }
    }
    public void increaseDeaths() {
        this.deaths++;
    }

    public void increaseXP() {
        this.xp++;
        updateXpBar();
    }

    public void increaseHealth() {
        player.setHealth(Math.min(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(),
            player.getHealth() + 4));
    }

    public void resetStreaks() {
        streaks = 0;
    }
}
