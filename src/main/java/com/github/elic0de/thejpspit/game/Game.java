package com.github.elic0de.thejpspit.game;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.player.PitPlayer;
import com.github.elic0de.thejpspit.scoreboard.GameScoreboard;
import com.github.elic0de.thejpspit.task.GameTask;
import de.themoep.minedown.MineDown;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.entity.Player;

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
        pit.addPitTeam(player.getPlayer());
    }

    public void leave(PitPlayer player) {
        pit.getDatabase().updateUserData(player);
        pitPlayers.remove(player);
        pit.removePitTeam(player.getPlayer());

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

        player.increaseDeaths();
        player.resetStreaks();
        player.resetItem();
        killer.increaseKills();
        killer.increaseXP();
        killer.increaseStreaks();
        killer.addReward();

        pit.getRatingHelper().initRating(player);
        pit.getRatingHelper().initRating(killer);

        player.sendMessage("&c【PIT】%player%に倒されました(KDレート:%rating%)"
            .replaceAll("%player%", killer.getName())
            .replaceAll("%rating%", killer.getRating() + "%")
        );
        killer.sendMessage("&b【PIT】%player%を倒しました(KDレート:%rating%)"
            .replaceAll("%player%", player.getName())
            .replaceAll("%rating%", player.getRating() + "%")
        );

        //KillAssistHelper.test(player.getPlayer());
    }

    public void broadcast(String message) {
        for (PitPlayer pitPlayer : getPitPlayers()) {
            final Player player = pitPlayer.getPlayer();

            player.spigot().sendMessage(new MineDown(message).toComponent());
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
