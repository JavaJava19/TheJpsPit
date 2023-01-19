package com.github.elic0de.thejpspit.spigot.player;

import com.github.elic0de.thejpspit.spigot.TheJpsPit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;

public class PitPlayerManager {

    private static final Map<UUID, PitPlayer> pitPlayerMap = new HashMap<>();

    public static void registerUser(PitPlayer player) {
        TheJpsPit.getInstance().getGame().join(player);
        pitPlayerMap.put(player.getUniqueId(), player);
    }

    public static void unregisterUser(PitPlayer player) {
        final UUID uuid = player.getUniqueId();
        pitPlayerMap.remove(uuid);
    }

    public static PitPlayer getPitPlayer(Player player) {
        return pitPlayerMap.get(player.getUniqueId());
    }
}
