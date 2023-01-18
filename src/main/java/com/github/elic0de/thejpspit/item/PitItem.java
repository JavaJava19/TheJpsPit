package com.github.elic0de.thejpspit.item;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Objects;

public abstract class PitItem {

    public abstract String getId();

    public abstract ItemStack getItemStack();

    public abstract int getPrice();

    public abstract int getRequiredLevel();

    public ItemStack getShopItem() {
        ItemStack item = getItemStack().clone();
        ItemMeta itemMeta = Objects.requireNonNull(item.getItemMeta());
        itemMeta.setLore(Arrays.asList(
                String.format("必要レベル: §e%d", getRequiredLevel()),
                String.format("値段: §e%d", getPrice())
        ));
        item.setItemMeta(itemMeta);
        return item;
    }
}
