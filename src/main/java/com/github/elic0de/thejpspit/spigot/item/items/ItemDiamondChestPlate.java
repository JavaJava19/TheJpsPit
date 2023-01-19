package com.github.elic0de.thejpspit.spigot.item.items;

import com.github.elic0de.thejpspit.spigot.item.PitItem;
import com.github.elic0de.thejpspit.spigot.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemDiamondChestPlate extends PitItem {
    @Override
    public String getId() {
        return "diamond_chestplate";
    }

    @Override
    public ItemStack getItemStack() {
        return ItemUtil.withUnbreakable(new ItemStack(Material.DIAMOND_CHESTPLATE));
    }

    @Override
    public int getPrice() {
        return 500;
    }

    @Override
    public int getRequiredLevel() {
        return 0;
    }

    @Override
    public String getName() {
        return "§aダイヤのチェストプレート";
    }
}
