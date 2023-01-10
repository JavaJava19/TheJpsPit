package com.github.elic0de.thejpspit.queue;

import com.github.elic0de.thejpspit.network.PluginMessageReceiver;
import com.github.elic0de.thejpspit.player.PitPlayer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class QueueManager {

    private final Set<PitPlayer> queuedBattlePlayer = new HashSet<>();
    private final Set<PitPlayer> queuedWoolPlayer = new HashSet<>();

    private final Map<QueueServerType, Integer> servers = new HashMap<>();

    private final int MIN_PLAYER_SIZE = 6;

    public void checkQueue() {
        for (QueueServerType type : QueueServerType.values()) {
            PluginMessageReceiver.sendServerPlayerCount(type.name());
            if (servers.isEmpty()) {
                continue;
            }
            final int playerSize = servers.get(type);
            final Set<PitPlayer> queuedPlayers =
                type == QueueServerType.WOOL_PVP ? queuedWoolPlayer : queuedBattlePlayer;

            if (MIN_PLAYER_SIZE < playerSize + queuedPlayers.size()) {
                sendServer(type);
            }
        }
    }

    public void addQueue(PitPlayer player, QueueServerType type) {
        /*cancelQueue(player);
        if (type == QueueServerType.BATTLE_CASTLE) {
            queuedBattlePlayer.add(player);
        } else {
            queuedWoolPlayer.add(player);
        }
        checkQueue();*/
    }

    public void cancelQueue(PitPlayer player) {
        queuedBattlePlayer.remove(player);
        queuedWoolPlayer.remove(player);
    }

    public void updateQueue(QueueServerType type, int playerCount) {
        servers.put(type, playerCount);
    }

    public boolean isQueued(PitPlayer player, QueueServerType type) {
        final Set<PitPlayer> queuedPlayers =
            type == QueueServerType.WOOL_PVP ? queuedWoolPlayer : queuedBattlePlayer;
        return queuedPlayers.contains(player);
    }

    private void sendServer(QueueServerType type) {
        final Set<PitPlayer> queuedPlayers =
            type == QueueServerType.WOOL_PVP ? queuedWoolPlayer : queuedBattlePlayer;

        for (PitPlayer player : queuedPlayers) {
            PluginMessageReceiver.changeServer(player, type.name());
        }
    }

    public int getNeededPlayer(QueueServerType type) {
        if (servers.isEmpty()) {
            return 6;
        }
        final Set<PitPlayer> queuedPlayers =
            type == QueueServerType.WOOL_PVP ? queuedWoolPlayer : queuedBattlePlayer;
        return Math.max(0, 6 - (queuedPlayers.size() + servers.get(type)));
    }
}
