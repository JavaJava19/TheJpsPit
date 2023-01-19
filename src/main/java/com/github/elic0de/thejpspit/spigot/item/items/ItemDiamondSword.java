package com.github.elic0de.thejpspit.spigot.item.items;

import com.github.elic0de.thejpspit.spigot.item.PitItemEntry;
import com.github.elic0de.thejpspit.spigot.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemDiamondSword extends PitItemEntry {

    @Override
    public String getId() {
        return "diamond_sword";
    }

    @Override
    public ItemStack getRawItemStack() {
        return ItemUtil.withUnbreakable(new ItemStack(Material.DIAMOND_SWORD));
    }

    @Override
    public int getPrice() {
        return 150;
    }

    @Override
    public int getRequiredLevel() {
        return 0;
    }

    @Override
    public String getName() {
        return "§aダイヤ剣";
    }
}
