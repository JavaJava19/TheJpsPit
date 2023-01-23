package com.github.elic0de.thejpspit.item.items;

import com.github.elic0de.thejpspit.item.PitItemEntry;
import com.github.elic0de.thejpspit.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemDiamondChestPlate extends PitItemEntry {

    @Override
    public String getId() {
        return "diamond_chestplate";
    }

    @Override
    public ItemStack getRawItemStack() {
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

    @Override
    public char getSlotChar() {
        return 'C';
    }
}
