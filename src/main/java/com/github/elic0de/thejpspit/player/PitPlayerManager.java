package com.github.elic0de.thejpspit.player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;

public class PitPlayerManager {

    private static final Map<UUID, PitPlayer> pitPlayerMap = new HashMap<>();

    public static void registerUser(PitPlayer player) {
        pitPlayerMap.put(player.getUniqueId(), player);
    }

    public static void unregisterUser(PitPlayer player) {
        final UUID uuid = player.getUniqueId();
        pitPlayerMap.remove(uuid);
    }

    public static PitPlayer getPitPlayer(Player player) {
        return pitPlayerMap.get(player.getUniqueId());
    }

    public static boolean isContain(Player player) {
        return pitPlayerMap.containsKey(player.getUniqueId());
    }

}
