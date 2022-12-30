package com.github.elic0de.thejpspit.game;

import com.github.elic0de.thejpspit.player.PitPlayer;
import com.github.elic0de.thejpspit.player.PitPlayerManager;
import com.github.elic0de.thejpspit.scoreboard.GameScoreboard;
import com.github.elic0de.thejpspit.task.GameTask;

import java.util.HashSet;
import java.util.Set;

public class Game {
    
    private final Set<PitPlayer> pitPlayers = new HashSet<>();
    private final GameScoreboard scoreboard;
    private final GameTask task;

    public Game() {
        this.scoreboard = new GameScoreboard();
        this.task = new GameTask();
    }
    
    public void join(PitPlayer player) {
        pitPlayers.add(player);
    }

    public void leave(PitPlayer player) {
        pitPlayers.remove(player);
    }
    
    public void death(PitPlayer player) {
        final PitPlayer victim = player;
        final PitPlayer killer = victim.getKiller();

        if (killer == null) return;

        victim.sendMessage("【PIT】az_akaに倒されました(KDレート:0.53%)");
        killer.sendMessage("【PIT】az_akaを倒しました(KDレート:0.53%)");
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
