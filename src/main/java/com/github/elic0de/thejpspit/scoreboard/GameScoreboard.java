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
            "",
            "K/Dレート: &c%rating%",
            "次のレベルまで：&a%neededXp%",
            "",
            "連続キル数: &a%streaks%",
            "",
            "&ejapanpvpserver.net"
        ).map(s ->
            s.replaceAll("%level%", Levels.getPlayerLevel(player) + "")
                .replaceAll("%neededXp%", Levels.getPlayerNeededXP(player) + "")
                .replaceAll("%rating%", player.getRating() + "%")
                .replaceAll("%streaks%", player.getStreaks() + "")
        ).collect(Collectors.toList());
    }

}
