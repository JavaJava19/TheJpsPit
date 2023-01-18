package com.github.elic0de.thejpspit.game;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.player.PitPlayer;
import com.github.elic0de.thejpspit.scoreboard.GameScoreboard;
import com.github.elic0de.thejpspit.task.GameTask;
import java.math.BigDecimal;
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
        player.updateDisplayName();
        player.setLastDamager(null);
    }

    public void leave(PitPlayer player) {
        pit.getDatabase().updateUserData(player);
        pitPlayers.remove(player);
    }

    public void death(PitPlayer player) {
        final PitPlayer killer = player.getKiller();

        if (killer == null) {
            player.increaseDeaths();
            player.resetItem();
            player.resetStreaks();

            pit.getRatingHelper().initRating(player);

            player.sendMessage("&c【PIT】死亡しました");
            return;
        }

        final long streaks = player.getStreaks();

        if (streaks > 5) {
            broadcast("&c【PIT】&a%killer%&7が&c%vitim%の&c%streaks%ストリーク&7を止めました！"
                .replaceAll("%killer%", killer.getName())
                .replaceAll("%vitim%", player.getName())
                .replaceAll("%streaks%", streaks + "")
            );
            TheJpsPit.getInstance().getEconomyHook().ifPresent(economyHook -> economyHook.giveMoney(player,
                BigDecimal.valueOf(streaks * 100)));
            killer.sendMessage(streaks * 100 + "の懸賞金がもらえます");
        }

        player.increaseDeaths();
        player.resetStreaks();
        player.resetItem();
        killer.increaseKills();
        killer.increaseXP();
        killer.increaseStreaks();
        killer.addReward();

        pit.getRatingHelper().initRating(player);
        pit.getRatingHelper().initRating(killer);

        pit.getAssistKillHelper().death(player);

        player.sendMessage("&c【PIT】%player%に倒されました(KDレート:%rating%)"
            .replaceAll("%player%", killer.getName())
            .replaceAll("%rating%", killer.getRating() + "%")
        );
        killer.sendMessage("&b【PIT】%player%を倒しました(KDレート:%rating%)"
            .replaceAll("%player%", player.getName())
            .replaceAll("%rating%", player.getRating() + "%")
        );

        player.setLastDamager(null);
        pit.getDatabase().updateUserData(player);
    }

    public void broadcast(String message) {
        for (PitPlayer pitPlayer : getPitPlayers()) {
            pitPlayer.sendMessage(message);
        }
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
