package com.github.elic0de.thejpspit.spigot.queue;

import com.github.elic0de.thejpspit.spigot.network.PluginMessageReceiver;
import com.github.elic0de.thejpspit.spigot.player.PitPlayer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class QueueManager {

    private final Map<PitPlayer, String> queuedPlayers = new HashMap<>();
    private final Map<String, Integer> servers = new HashMap<>();

    private final Map<PitPlayer, String> commands = new HashMap<>();

    private final int MIN_PLAYER_SIZE = 2;

    public void checkQueue() {
        PluginMessageReceiver.sendServerPlayerCount();
        if (servers.isEmpty()) return;

        servers.forEach((s, integer) -> {
            if (getNeededPlayer(s) == 0) executeCommand(s);
        });
    }

    public void addQueue(PitPlayer player, String serverName, String command) {
        queuedPlayers.remove(player);
        queuedPlayers.put(player, serverName);
        commands.put(player, command);
        checkQueue();
    }

    public void cancelQueue(PitPlayer player) {
        queuedPlayers.remove(player);
    }

    public void updateQueue(String serverName, int playerCount) {
        servers.put(serverName, playerCount);
    }

    public boolean isQueued(PitPlayer player, String serverName) {
        final String queuedServerName = queuedPlayers.get(player);
        if (queuedServerName == null) return false;
        return queuedServerName.equals(serverName);
    }

    private void executeCommand(String serverName) {
        for (PitPlayer player : queuedPlayers.keySet()) {
            if (!Objects.equals(queuedPlayers.get(player), serverName)) return;
            final String command = commands.getOrDefault(player,"");
            if (command.equals("")) return;
            player.getPlayer().performCommand(command);
            cancelQueue(player);
        }
    }

    public int getNeededPlayer(String serverName) {
        if (servers.isEmpty()) {
            return MIN_PLAYER_SIZE;
        }
        int totalPlayers = 0;
        if (servers.containsKey(serverName)) totalPlayers = servers.get(serverName);
        totalPlayers = totalPlayers + (int) queuedPlayers.values().stream().filter(
            serverName::equalsIgnoreCase).count();
        return Math.max(0, MIN_PLAYER_SIZE - totalPlayers);
    }
}
