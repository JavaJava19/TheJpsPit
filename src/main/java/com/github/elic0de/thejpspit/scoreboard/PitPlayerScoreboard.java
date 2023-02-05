package com.github.elic0de.thejpspit.scoreboard;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.leveler.Levels;
import com.github.elic0de.thejpspit.player.PitPlayer;
import fr.mrmicky.fastboard.FastBoard;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;

public class PitPlayerScoreboard {

    private final FastBoard board;
    private final PitPlayer player;
    public PitPlayerScoreboard(PitPlayer player) {
        this.player = player;
        this.board = new FastBoard(player.getPlayer());
        this.board.updateTitle(ChatColor.translateAlternateColorCodes('&', "&eTHE JPS PIT"));
        this.board.updateLines(
            init(player).stream().map(s -> ChatColor.translateAlternateColorCodes('&', s))
                .collect(Collectors.toList()));
    }

    public void destoryScoreboard() {
        if (board != null) board.delete();
    }
    public void updateLevel() {
        board.updateLine(0, "レベル: [%level%]".replaceAll("%level%", Levels.getPlayerLevelColor(player.getLevel()) + "" + player.getLevel() + ChatColor.RESET));
    }

    public void updateCoins() {
        TheJpsPit.getInstance().getEconomyHook().ifPresent(economyHook -> board.updateLine(1, "JP: [%coins%]".replaceAll("%coins%", economyHook.getBalance(player).toPlainString())));
    }

    public void updateRating() {
        board.updateLine(3, "K/Dレート: &c%rating%".replaceAll("%rating%",  player.getRating() + ""));
    }

    public void updateBestRating() {
        board.updateLine(4, "最高レート: &b%bestRating%".replaceAll("%bestRating%", player.getBestRating() + ""));
    }

    public void updateNeededXp() {
        board.updateLine(6, "次のレベルまで：&a%neededXp%".replaceAll("%neededXp%", Levels.getPlayerNeededXP(player.getLevel(),
            (int) player.getXp()) + ""));
    }

    public void updateKillStreaks() {
        board.updateLine(9, "連続キル数: &a%streaks%".replaceAll("%streaks%", player.getStreaks() + ""));
    }

    public void updateBestKillStreaks() {
        board.updateLine(10, "最高連続キル数: &a%bestStreaks%".replaceAll("%bestStreaks%", player.getBestStreaks() + ""));
    }

    private List<String> init(PitPlayer player) {
        return Stream.of(
                "",
                "レベル: [%level%]",
                "JP: [%coins%]",
                "",
                "K/Dレート: &c%rating%",
                "最高レート: &b%bestRating%",
                "",
                "次のレベルまで：&a%neededXp%",
                "",
                "連続キル数: &a%streaks%",
                "最高連続キル数: &a%bestStreaks%",
                "",
                "&ejapanpvpserver.net"
            ).map(s -> PlaceholderAPI.setPlaceholders(player.getPlayer(), s)).map(s ->
            s.replaceAll("%level%",   Levels.getPlayerLevelColor(player.getLevel()) + "" + player.getLevel() + ChatColor.RESET)
                .replaceAll("%neededXp%", Levels.getPlayerNeededXP(player.getLevel(),
                    (int) player.getXp()) + "")
                .replaceAll("%rating%", player.getRating() + "%")
                .replaceAll("%bestRating%", player.getBestRating() + "%")
                .replaceAll("%streaks%", player.getStreaks() + "")
                .replaceAll("%bestStreaks%", player.getBestStreaks() + "")
                .replaceAll("%coins", player.coins().toPlainString())
        ).collect(Collectors.toList());
    }
}
