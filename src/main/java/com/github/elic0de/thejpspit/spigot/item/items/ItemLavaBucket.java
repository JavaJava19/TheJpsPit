package com.github.elic0de.thejpspit.spigot.item.items;

import com.github.elic0de.thejpspit.spigot.item.PitItemEntry;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemLavaBucket extends PitItemEntry {
    @Override
    public String getId() {
        return "lava_bucket";
    }

    @Override
    public ItemStack getRawItemStack() {
        return new ItemStack(Material.LAVA_BUCKET);
    }

    @Override
    public int getPrice() {
        return 100;
    }

    @Override
    public int getRequiredLevel() {
        return 0;
    }

    @Override
    public String getName() {
        return "§aマグマバケツ";
    }

    @Override
    public char getSlotChar() {
        return 'L';
    }
}
