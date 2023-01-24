package com.github.elic0de.thejpspit.item.items;

import com.github.elic0de.thejpspit.item.PitItemEntry;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemCobweb extends PitItemEntry {

    @Override
    public String getId() {
        return "cobweb";
    }

    @Override
    public ItemStack getRawItemStack() {
        return new ItemStack(Material.COBWEB, 5);
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
        return "§aクモの巣";
    }

    @Override
    public char getSlotChar() {
        return 'c';
    }
}
