package com.github.elic0de.thejpspit.spigot.item.items;

import com.github.elic0de.thejpspit.spigot.item.PitItemEntry;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemObsidian extends PitItemEntry {

    @Override
    public String getId() {
        return "obsidian";
    }

    @Override
    public ItemStack getRawItemStack() {
        return new ItemStack(Material.OBSIDIAN, 10);
    }

    @Override
    public int getPrice() {
        return 3000;
    }

    @Override
    public int getRequiredLevel() {
        return 0;
    }

    @Override
    public String getName() {
        return "§a黒曜石 §fx10";
    }
}
