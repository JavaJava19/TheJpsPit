package com.github.elic0de.thejpspit.item.items;

import com.github.elic0de.thejpspit.item.PitItemEntry;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemEnchantedGoldenApple extends PitItemEntry {

    @Override
    public String getId() {
        return "enchanted_golden_apple";
    }

    @Override
    public ItemStack getRawItemStack() {
        return new ItemStack(Material.ENCHANTED_GOLDEN_APPLE);
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
        return "上位金りんご";
    }

    @Override
    public char getSlotChar() {
        return 'G';
    }
}
