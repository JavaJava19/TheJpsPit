package com.github.elic0de.thejpspit.scoreboard;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.leveler.Levels;
import com.github.elic0de.thejpspit.player.PitPlayer;
import fr.mrmicky.fastboard.FastBoard;
import java.util.List;
import java.util.stream.Collectors;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;

public class PitPlayerScoreboard {

    private FastBoard board;
    private PitPlayer player;

    public PitPlayerScoreboard(PitPlayer player) {
        this.player = player;
        this.board = new FastBoard(player.getPlayer());
        board.updateLines(
            boardLines(player).stream().map(s -> ChatColor.translateAlternateColorCodes('&', s))
                .collect(Collectors.toList()));
    }

    public void updateKill() {
        //board.updateLine();
    }

    private List<String> boardLines(PitPlayer player) {
        return TheJpsPit.getInstance().getSettings().getScoreboard().stream().map(s -> PlaceholderAPI.setPlaceholders(player.getPlayer(), s)).map(s ->
            s.replaceAll("%level%",   Levels.getPlayerLevelColor(player.getLevel()) + "" + player.getLevel() + ChatColor.RESET)
                .replaceAll("%neededXp%", Levels.getPlayerNeededXP(player.getLevel(),
                    (int) player.getXp()) + "")
                .replaceAll("%rating%", player.getRating() + "%")
                .replaceAll("%bestRating%", player.getBestRating() + "%")
                .replaceAll("%streaks%", player.getStreaks() + "")
                .replaceAll("%bestStreaks%", player.getBestStreaks() + "")
        ).collect(Collectors.toList());
    }
}
