package com.github.elic0de.thejpspit.item.items;

import com.github.elic0de.thejpspit.item.PitItemEntry;
import com.github.elic0de.thejpspit.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemDiamondBoots extends PitItemEntry {

    @Override
    public String getId() {
        return "diamond_boots";
    }

    @Override
    public ItemStack getRawItemStack() {
        return ItemUtil.withUnbreakable(new ItemStack(Material.DIAMOND_BOOTS));
    }

    @Override
    public int getPrice() {
        return 300;
    }

    @Override
    public int getRequiredLevel() {
        return 0;
    }

    @Override
    public String getName() {
        return "§aダイヤモンドの靴";
    }

    @Override
    public char getSlotChar() {
        return 'B';
    }
}
