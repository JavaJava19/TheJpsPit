package com.github.elic0de.thejpspit.villager.villagers;

import com.github.elic0de.thejpspit.villager.VillagerNPC;
import org.bukkit.entity.Player;

public class ShopVillager extends VillagerNPC {
    @Override
    public String getId() {
        return "Shop";
    }

    @Override
    public String getName() {
        return "&e&lショップ";
    }

    @Override
    protected void onClick(Player clickedPlayer) {
        //@TODO implements
    }
}
