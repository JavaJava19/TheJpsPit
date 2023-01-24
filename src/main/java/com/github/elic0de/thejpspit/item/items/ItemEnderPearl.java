package com.github.elic0de.thejpspit.item.items;

import com.github.elic0de.thejpspit.item.PitItemEntry;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemEnderPearl extends PitItemEntry {

    @Override
    public String getId() {
        return "ender_pearl";
    }

    @Override
    public ItemStack getRawItemStack() {
        return new ItemStack(Material.ENDER_PEARL);
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
        return "§aエンダーパール";
    }

    @Override
    public char getSlotChar() {
        return 'e';
    }
}
