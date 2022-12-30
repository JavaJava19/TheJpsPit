package com.github.elic0de.thejpspit.game;

import com.github.elic0de.thejpspit.player.PitPlayer;
import com.github.elic0de.thejpspit.player.PitPlayerManager;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class Game {
    
    private final Set<PitPlayer> pitPlayers = new HashSet<>();
    
    public void join(PitPlayer player) {

    }

    public void leave(PitPlayer player) {

    }
    
    public void death(PitPlayer player) {
        final PitPlayer victim = PitPlayerManager.getPitPlayer(player.getPlayer());
        final PitPlayer killer = victim.getKiller();

        if (killer == null) return;

        victim.sendMessage("【PIT】az_akaを倒されました(KDレート:0.53%)");
        killer.sendMessage("【PIT】az_akaを倒しました(KDレート:0.53%)");
    }

    public Set<PitPlayer> getPitPlayers() {
        return pitPlayers;
    }
}
