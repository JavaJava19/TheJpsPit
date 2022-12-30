package com.github.elic0de.thejpspit.scoreboard;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.game.Game;
import com.github.elic0de.thejpspit.player.PitPlayer;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GameScoreboard {

    public void update() {
        final Game game = TheJpsPit.getInstance().getGame();
        for (PitPlayer player : game.getPitPlayers()) {
            player.getBoard().updateLines(boardLines(player).stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList()));
        }
    }

    public List<String> boardLines(PitPlayer player) {

        return Arrays.asList(
                "",
                "レベル:[%level%]",
                "",
                "K/Dレート",
                "次のレベルまで：%nextLevel%",
                "",
                "japanpvpserver.net"
        ).stream().map(s ->
                s.replaceAll("%level%", "none")
        ).collect(Collectors.toList());
    }

}
