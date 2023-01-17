package com.github.elic0de.thejpspit.villager;

import com.github.elic0de.thejpspit.TheJpsPit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.persistence.PersistentDataType;

public abstract class VillagerNPC {

    public static final NamespacedKey npcIdKey = new NamespacedKey(TheJpsPit.getInstance(), "npcId");

    public abstract String getId();

    public abstract String getName();

    protected abstract void onClick(Player clickedPlayer);

    public void click(Player clickedPlayer) {
        onClick(clickedPlayer);
    }

    public Villager spawnAt(World world, Location location) {
        Villager villager = world.spawn(location, Villager.class);
        villager.setVillagerType(getVillagerType());
        villager.setProfession(getProfession());
        villager.setCustomNameVisible(true);
        villager.setCustomName(getName());
        villager.getPersistentDataContainer().set(npcIdKey, PersistentDataType.STRING, getId());
        return villager;
    }

    public Villager.Type getVillagerType() {
        return Villager.Type.PLAINS;
    }

    public Villager.Profession getProfession() {
        return Villager.Profession.NONE;
    }
}
