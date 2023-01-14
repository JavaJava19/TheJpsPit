package com.github.elic0de.thejpspit.scoreboard;

import com.github.elic0de.thejpspit.leveler.Levels;
import com.github.elic0de.thejpspit.player.PitPlayer;
import com.github.elic0de.thejpspit.player.PitPlayerManager;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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

        return Stream.of(
            "",
            "レベル:[%level%]",
            "最高レート: &b%bestRating%",
            "",
            "K/Dレート: &c%rating%",
            "次のレベルまで：&a%neededXp%",
            "",
            "連続キル数: &a%streaks%",
            "最高連続キル数: &a%bestStreaks%",
            "",
            "&ejapanpvpserver.net"
        ).map(s ->
            s.replaceAll("%level%", Levels.getPlayerLevel(player) + "")
                .replaceAll("%neededXp%", Levels.getPlayerNeededXP(player) + "")
                .replaceAll("%rating%", player.getRating() + "%")
                .replaceAll("%bestRating%", player.getBestRating() + "%")
                .replaceAll("%streaks%", player.getStreaks() + "")
                .replaceAll("%bestStreaks%", player.getBestStreaks() + "")
        ).collect(Collectors.toList());
    }

}
