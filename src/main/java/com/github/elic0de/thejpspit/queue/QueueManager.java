package com.github.elic0de.thejpspit.queue;

import com.github.elic0de.thejpspit.player.PitPlayer;

import java.util.HashSet;
import java.util.Set;

public class QueueManager {

    private final Set<PitPlayer> queuedBattlePlayer = new HashSet<>();
    private final Set<PitPlayer> queuedWoolPlayer = new HashSet<>();

    public void checkQueue() {

    }

    public void addQueue(PitPlayer player, ServerQueueType type) {
        if (type == ServerQueueType.BATTLE_CASTLE) queuedBattlePlayer.add(player);
        else queuedWoolPlayer.add(player);
        checkQueue();
    }
}
