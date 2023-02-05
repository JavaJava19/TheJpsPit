package com.github.elic0de.thejpspit.scoreboard;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.hook.EconomyHook;
import com.github.elic0de.thejpspit.leveler.Levels;
import com.github.elic0de.thejpspit.player.PitPlayer;
import com.github.elic0de.thejpspit.player.PitPlayerManager;

import de.themoep.minedown.MineDown;
import de.themoep.minedown.MineDownParser;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class GameScoreboard {

    public void update() {
        for (PitPlayer player : Bukkit.getOnlinePlayers().stream()
            .map(PitPlayerManager::getPitPlayer).toList()) {
            player.getBoard().updateLines(
                boardLines(player).stream().map(s -> ChatColor.translateAlternateColorCodes('&', s))
                    .collect(Collectors.toList()));
        }
    }

    public List<String> boardLines(PitPlayer player) {
        return TheJpsPit.getInstance().getSettings().getScoreboard().stream().map(s -> PlaceholderAPI.setPlaceholders(player.getPlayer(), s)).map(s ->
                s.replaceAll("%level%",   Levels.getPlayerLevelColor(player.getLevel()) + "" + player.getLevel() + ChatColor.RESET)
                        .replaceAll("%neededXp%", Levels.getPlayerNeededXP(player.getLevel(), (int) player.getXp()) + "")
                        .replaceAll("%rating%", player.getRating() + "%")
                        .replaceAll("%bestRating%", player.getBestRating() + "%")
                        .replaceAll("%streaks%", player.getStreaks() + "")
                        .replaceAll("%bestStreaks%", player.getBestStreaks() + "")
                        .replaceAll("%coins%", player.coins().toPlainString())
        ).collect(Collectors.toList());
    }

}
