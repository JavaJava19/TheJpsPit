package com.github.elic0de.thejpspit.item.items;

import com.github.elic0de.thejpspit.item.PitItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemObsidian extends PitItem {

    @Override
    public String getId() {
        return "obsidian";
    }

    @Override
    public ItemStack getItemStack() {
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
