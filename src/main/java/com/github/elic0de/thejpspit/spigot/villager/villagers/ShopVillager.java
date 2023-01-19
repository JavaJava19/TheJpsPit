package com.github.elic0de.thejpspit.spigot.villager.villagers;

import com.github.elic0de.thejpspit.spigot.TheJpsPit;
import com.github.elic0de.thejpspit.spigot.gui.ShopMenu;
import com.github.elic0de.thejpspit.spigot.player.PitPlayerManager;
import com.github.elic0de.thejpspit.spigot.villager.VillagerNPC;
import org.bukkit.entity.Player;

public class ShopVillager extends VillagerNPC {
    @Override
    public String getId() {
        return "shop";
    }

    @Override
    public String getName() {
        return "§e§lショップ";
    }

    @Override
    protected void onClick(Player clickedPlayer) {
        ShopMenu.create(TheJpsPit.getInstance(), "ショップ").show(PitPlayerManager.getPitPlayer(clickedPlayer));
    }
}
