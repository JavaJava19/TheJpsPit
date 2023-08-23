package com.github.elic0de.thejpspit.game;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.player.PitPlayer;
import com.github.elic0de.thejpspit.task.GameTask;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;

public class Game {

    private final TheJpsPit pit = TheJpsPit.getInstance();
    private final Set<PitPlayer> pitPlayers = new HashSet<>();
    private final GameTask task;

    public Game() {
        this.task = new GameTask();
    }

    public void join(PitPlayer player) {
        pitPlayers.add(player);
        player.addItem();
        player.updateDisplayName();
        player.setLastDamager(null);
    }

    public void leave(PitPlayer player) {
        pitPlayers.remove(player);
        pit.getDatabase().updateUserData(player);
    }

    public void death(PitPlayer player) {
        final PitPlayer killer = player.getKiller();

        // 死んだらテレポートをさせる
        pit.getPitPreferences().ifPresent(pitPreferences -> {
            final Location location = pitPreferences.getSpawn().get().getLocation();

            if (location != null) player.getPlayer().teleport(location);
        });

        if (killer == player)  {
            player.increaseDeaths();
            player.resetItem();
            player.resetStreaks();
            player.sendMessage("&c【PIT】死亡しました");

            return;
        }

        if (killer == null) {
            player.increaseDeaths();
            player.resetItem();
            player.resetStreaks();

            pit.getRatingHelper().initRating(player);
            pit.getCosmeticManager().onDeath(player);
            player.sendMessage("&c【PIT】死亡しました");

            return;
        }

        final long streaks = player.getStreaks();

        if (streaks > 4) {
            streakBroadcast("&c【PIT】&a%killer%&7が&c%vitim%の&c%streaks%ストリーク&7を止めました！"
                .replaceAll("%killer%", killer.getName())
                .replaceAll("%vitim%", player.getName())
                .replaceAll("%streaks%", streaks + "")
            );
            TheJpsPit.getInstance().getEconomyHook().ifPresent(economyHook -> {
                economyHook.giveMoney(killer, BigDecimal.valueOf(50));
                killer.sendMessage(50 + "JP+");
            });
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

        player.getPreferences().ifPresent(preferences -> {
            if (preferences.isDeathMessage()) {
                player.sendMessage("&c【PIT】%player%に倒されました(KDレート:%rating%)"
                    .replaceAll("%player%", killer.getName())
                    .replaceAll("%rating%", killer.getRating() + "%")
                );
            }
        });
        killer.getPreferences().ifPresent(preferences -> {
            if (preferences.isKillMessage()) {
                killer.sendMessage("&b【PIT】%player%を倒しました(KDレート:%rating%)"
                    .replaceAll("%player%", player.getName())
                    .replaceAll("%rating%", player.getRating() + "%")
                );
            }
        });
        pit.getCosmeticManager().onKill(killer, player);
        pit.getCosmeticManager().onDeath(player);

        player.setLastDamager(null);
        pit.getDatabase().updateUserData(player);
    }

    public void streakBroadcast(String message) {
        for (PitPlayer pitPlayer : getPitPlayers()) {
            pitPlayer.getPreferences().ifPresent(preferences -> {
                if (preferences.isStreaksMessage()) {
                    pitPlayer.sendMessage(message);
                }
            });
        }
    }

    public Set<PitPlayer> getPitPlayers() {
        return pitPlayers;
    }

    public GameTask getTask() {
        return task;
    }
}
