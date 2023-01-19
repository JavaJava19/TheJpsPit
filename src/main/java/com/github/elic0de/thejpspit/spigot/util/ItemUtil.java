package com.github.elic0de.thejpspit.spigot.util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class ItemUtil {
    public static ItemStack withUnbreakable(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        Objects.requireNonNull(itemMeta).setUnbreakable(true);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
