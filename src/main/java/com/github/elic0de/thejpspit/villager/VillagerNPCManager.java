package com.github.elic0de.thejpspit.villager;

import org.bukkit.entity.Villager;
import org.bukkit.persistence.PersistentDataType;

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

    public static VillagerNPC getVillagerNPC(String id) {
        return villagerNPCMap.get(id);
    }

    public static VillagerNPC getVillagerNPC(Villager villager) {
        final String npcId = villager.getPersistentDataContainer().get(VillagerNPC.npcIdKey, PersistentDataType.STRING);
        return villagerNPCMap.get(npcId);
    }
}
