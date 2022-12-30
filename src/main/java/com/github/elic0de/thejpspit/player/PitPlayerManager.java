package com.github.elic0de.thejpspit.player;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PitPlayerManager {

    private static final Map<UUID, PitPlayer> pitPlayerHashMap = new HashMap<>();
    public static void registerUser(Player player) {
        pitPlayerHashMap.put(player.getUniqueId(), new PitPlayer(player));
    }

    public static void unregisterUser(Player player) {
        final UUID uuid = player.getUniqueId();
        pitPlayerHashMap.remove(uuid);
    }

    public static PitPlayer getPitPlayer(Player player) {
        return pitPlayerHashMap.get(player.getUniqueId());
    }

    public static boolean isContain(Player player) {
        return pitPlayerHashMap.containsKey(player.getUniqueId());
    }

}
