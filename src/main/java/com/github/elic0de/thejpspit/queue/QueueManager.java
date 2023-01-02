package com.github.elic0de.thejpspit.queue;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.player.PitPlayer;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class QueueManager {

    private final Set<PitPlayer> queuedBattlePlayer = new HashSet<>();
    private final Set<PitPlayer> queuedWoolPlayer = new HashSet<>();

    private final Map<QueueServerType, Integer> servers =  new HashMap<>();

    private final int MIN_PLAYER_SIZE = 6;

    public void checkQueue() {
        for (QueueServerType type : QueueServerType.values()) {
            final int playerSize = servers.get(type);
            final Set<PitPlayer> queuedPlayers = type == QueueServerType.WOOL_PVP ? queuedWoolPlayer : queuedBattlePlayer;

            if (MIN_PLAYER_SIZE < playerSize + queuedPlayers.size()) {
                sendServer(type);
            }
        }
    }

    public void addQueue(PitPlayer player, QueueServerType type) {
        if (type == QueueServerType.BATTLE_CASTLE) queuedBattlePlayer.add(player);
        else queuedWoolPlayer.add(player);
        checkQueue();
    }

    public void updateQueue(QueueServerType type, int playerCount) {
        servers.put(type, playerCount);
    }

    private void sendServer(QueueServerType type) {
        final Set<PitPlayer> queuedPlayers = type == QueueServerType.WOOL_PVP ? queuedWoolPlayer : queuedBattlePlayer;

        for (PitPlayer player : queuedPlayers) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF(type.name());
            player.getPlayer().sendPluginMessage(TheJpsPit.getInstance(), "BungeeCord", out.toByteArray());
        }
    }
}
