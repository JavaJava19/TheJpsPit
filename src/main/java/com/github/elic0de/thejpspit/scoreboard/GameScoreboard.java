package com.github.elic0de.thejpspit.scoreboard;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.game.Game;
import com.github.elic0de.thejpspit.player.PitPlayer;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameScoreboard {

    public void update() {
        final Game game = TheJpsPit.getInstance().getGame();
        for (PitPlayer player : game.getPitPlayers()) {
            player.getBoard().updateLines(boardLines(player).stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList()));
        }
    }

    public List<String> boardLines(PitPlayer player) {

        return Stream.of(
                "",
                "レベル:[%level%]",
                "",
                "K/Dレート: &c%rating%",
                "次のレベルまで：&a%nextLevel%",
                "",
                "&ejapanpvpserver.net"
        ).map(s ->
                s.replaceAll("%level%", "none")
                        .replaceAll("%rating%", player.getRating() + "%")
        ).collect(Collectors.toList());
    }

}
