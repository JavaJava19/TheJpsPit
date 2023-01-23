package com.github.elic0de.thejpspit.player;

import com.github.elic0de.thejpspit.database.Database;
import com.github.elic0de.thejpspit.leveler.Levels;
import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.util.ShowHealth;
import de.themoep.minedown.MineDown;
import fr.mrmicky.fastboard.FastBoard;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Stream;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
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
    };
    private final ItemStack[] ARMOR = {
        new ItemStack(Material.CHAINMAIL_BOOTS),
        new ItemStack(Material.IRON_LEGGINGS),
        new ItemStack(Material.IRON_CHESTPLATE)
    };
    private int level;
    private long kills;
    private long streaks;
    private long bestStreaks;
    private long deaths;
    private double rating;
    private double bestRating;
    private double xp;
    private final FastBoard board;
    private PitPlayer lastDamager;

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
        this.level = Levels.getPlayerLevel(this);
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
        this.level = Levels.getPlayerLevel(this);
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
        TheJpsPit.getInstance().getPitPreferences().ifPresent(pitPreferences -> {
            TheJpsPit.getInstance().getEconomyHook().ifPresent(economyHook -> economyHook.giveMoney(this, BigDecimal.valueOf(pitPreferences.getAmountReward())));
        });
    }

    public void showHealth(PitPlayer targetPit) {
        ShowHealth.showHealth(this, targetPit);
    }

    public void sendMessage(String message) {
        player.spigot().sendMessage(new MineDown(message).toComponent());
    }

    public void sendStatus() {
        sendStatus(this);
    }

    public void sendStatus(PitPlayer player) {
        final TheJpsPit pit = TheJpsPit.getInstance();
        Stream.of(
            "+ -----< %playerName% >----- +",
            "Kills >> &e%kills% (#%kills_ranking%)",
            "Best Kill Streaks >> &e%best_streaks%",
            "Deaths >> &c%deaths% (#%deaths_ranking%)",
            "Rating >> &a%rating% (#%rating_ranking%)",
            "Best Rating >> &e%best_rating%"
        ).map(s ->
            s.replaceAll("%playerName%", player.getName())
                .replaceAll("%kills%",
                    player.getKills() + "")
                .replaceAll("%best_streaks%",
                    player.getBestStreaks() + "")
                .replaceAll("%deaths%",
                    player.getDeaths() + "")
                .replaceAll("%rating%",
                    player.getRating() + "%")
                .replaceAll("%best_rating%",
                    player.getBestRating() + "")
                .replaceAll("%kills_ranking%",
                    pit.getDatabase().getPlayerRanking(player, Database.RankType.KILLS).join()
                        .orElse(0)
                        + "")
                .replaceAll("%deaths_ranking%",
                    pit.getDatabase().getPlayerRanking(player, Database.RankType.DEATHS).join()
                        .orElse(0)
                        + "")
                .replaceAll("%rating_ranking%",
                    pit.getDatabase().getPlayerRanking(player, Database.RankType.RATING).join()
                        .orElse(0)
                        + "")
        ).forEach(this::sendMessage);
    }

    private void updateXpBar() {
        // TODO 一時保存しておく
        final float xp = Levels.getPlayerNeededXP(level, (int) this.xp);
        final float totalXp = Levels.getLevelTotalXP(level);

        player.setLevel(level);
        player.setExp((totalXp - xp)/totalXp);
    }

    public void updateDisplayName() {
        final ChatColor color = Levels.getPlayerLevelColor(level);
        player.setDisplayName("[" + color + level + ChatColor.RESET + "]" + " " + getName());
        player.setPlayerListName("[" + color + level + ChatColor.RESET + "]" + " " + getName());
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
        if (player.getKiller() == null) return lastDamager;
        return PitPlayerManager.getPitPlayer(player.getKiller());
    }

    public int getLevel() {
        return level;
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

        // レベルアップ
        if (Levels.getPlayerNeededXP(level, (int) xp) == 0) {
            final int nextLevel = level + 1;
            final int previousLevel = this.level;
            this.level = nextLevel;
            player.sendTitle("§b§lLEVEL UP!", previousLevel + " → " + nextLevel, 20,40, 20);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
            updateDisplayName();
        }
        updateXpBar();
    }

    public void setLastDamager(PitPlayer player) {
        this.lastDamager = player;
    }

    public void resetStreaks() {
        streaks = 0;
    }
}
