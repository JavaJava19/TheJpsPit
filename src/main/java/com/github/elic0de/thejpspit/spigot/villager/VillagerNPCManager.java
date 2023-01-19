package com.github.elic0de.thejpspit.spigot.villager;

import org.bukkit.entity.Villager;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class VillagerNPCManager {

    private static final Map<String, VillagerNPC> villagerNPCMap = new HashMap<>();

    public static void register(VillagerNPC villagerNPC) {
        villagerNPCMap.put(villagerNPC.getId(), villagerNPC);
    }

    public static void clear() {
        villagerNPCMap.clear();
    }

    @Nullable
    public static VillagerNPC getVillagerNPC(String id) {
        return villagerNPCMap.get(id);
    }

    @Nullable
    public static VillagerNPC getVillagerNPC(Villager villager) {
        String npcId = villager.getPersistentDataContainer().get(VillagerNPC.npcIdKey, PersistentDataType.STRING);
        return getVillagerNPC(npcId);
    }

    public static boolean isVillagerNPC(Villager villager) {
        return villager.getPersistentDataContainer().get(VillagerNPC.npcIdKey, PersistentDataType.STRING) != null;
    }
}
