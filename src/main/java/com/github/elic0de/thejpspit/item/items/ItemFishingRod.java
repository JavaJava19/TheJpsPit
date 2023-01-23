package com.github.elic0de.thejpspit.item.items;

import com.github.elic0de.thejpspit.item.PitItemEntry;
import com.github.elic0de.thejpspit.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemFishingRod extends PitItemEntry {

    @Override
    public String getId() {
        return "fishing_rod";
    }

    @Override
    public ItemStack getRawItemStack() {
        return ItemUtil.withUnbreakable(new ItemStack(Material.FISHING_ROD));
    }

    @Override
    public int getPrice() {
        return 200;
    }

    @Override
    public int getRequiredLevel() {
        return 0;
    }

    @Override
    public String getName() {
        return "§a釣り竿";
    }

    @Override
    public char getSlotChar() {
        return 'f';
    }
}
