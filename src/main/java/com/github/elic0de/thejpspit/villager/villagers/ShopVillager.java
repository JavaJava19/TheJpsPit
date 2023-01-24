package com.github.elic0de.thejpspit.villager.villagers;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.gui.ShopMenu;
import com.github.elic0de.thejpspit.player.PitPlayerManager;
import com.github.elic0de.thejpspit.villager.VillagerNPC;
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
