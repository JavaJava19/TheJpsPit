package com.github.elic0de.thejpspit.scoreboard;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.game.Game;
import com.github.elic0de.thejpspit.leveler.Levels;
import com.github.elic0de.thejpspit.player.PitPlayer;
import com.github.elic0de.thejpspit.player.PitPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameScoreboard {

    public void update() {
        for (PitPlayer player : Bukkit.getOnlinePlayers().stream().map(player -> PitPlayerManager.getPitPlayer(player)).toList()) {
            player.getBoard().updateLines(boardLines(player).stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList()));
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
                "&ejapanpvpserver.net"
        ).map(s ->
                s.replaceAll("%level%", Levels.getPlayerLevel(player) + "")
                        .replaceAll("%neededXp%", Levels.getPlayerNeededXP(player) + "")
                        .replaceAll("%rating%", player.getRating() + "%")
        ).collect(Collectors.toList());
    }

}
