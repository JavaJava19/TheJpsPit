package com.github.elic0de.thejpspit.game;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.player.PitPlayer;
import com.github.elic0de.thejpspit.scoreboard.GameScoreboard;
import com.github.elic0de.thejpspit.task.GameTask;
import java.util.HashSet;
import java.util.Set;

public class Game {

    private final TheJpsPit pit = TheJpsPit.getInstance();
    private final Set<PitPlayer> pitPlayers = new HashSet<>();
    private final GameScoreboard scoreboard;
    private final GameTask task;

    public Game() {
        this.scoreboard = new GameScoreboard();
        this.task = new GameTask();
    }

    public void join(PitPlayer player) {
        pitPlayers.add(player);
        player.addItem();
        player.getBoard().updateLines();
    }

    public void leave(PitPlayer player) {
        pit.getDatabase().updateUserData(player);
        pitPlayers.remove(player);
    }

    public void death(PitPlayer player) {
        final PitPlayer killer = player.getKiller();

        if (killer == null) {
            return;
        }

        player.increaseDeaths();
        killer.increaseKills();
        killer.increaseXP();

        pit.getRatingHelper().initRating(player);
        pit.getRatingHelper().initRating(killer);

        player.sendMessage("【PIT】%player%に倒されました(KDレート:%rating%)"
            .replaceAll("%player%", killer.getName())
            .replaceAll("%rating%", killer.getRating() + "%")
        );
        killer.sendMessage("【PIT】%player%を倒しました(KDレート:%rating%)"
            .replaceAll("%player%", player.getName())
            .replaceAll("%rating%", player.getRating() + "%")
        );
    }

    public Set<PitPlayer> getPitPlayers() {
        return pitPlayers;
    }

    public GameScoreboard getScoreboard() {
        return scoreboard;
    }

    public GameTask getTask() {
        return task;
    }
}
