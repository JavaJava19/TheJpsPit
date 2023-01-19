package com.github.elic0de.thejpspit.spigot.item.items;

import com.github.elic0de.thejpspit.spigot.item.PitItem;
import com.github.elic0de.thejpspit.spigot.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemDiamondSword extends PitItem {

    @Override
    public String getId() {
        return "diamond_sword";
    }

    @Override
    public ItemStack getItemStack() {
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
