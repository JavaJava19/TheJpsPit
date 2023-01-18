package com.github.elic0de.thejpspit.item;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public abstract class PitItem {

    public abstract String getId();

    public abstract ItemStack getItemStack();

    public abstract int getPrice();

    public abstract int getRequiredLevel();

    public abstract String getName();

    public ItemStack getShopItem() {
        ItemStack item = getItemStack().clone();
        ItemMeta itemMeta = Objects.requireNonNull(item.getItemMeta());
        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(itemMeta);
        return item;
    }

    public String[] getLore() {
        return new String[]{
                getName(),
                String.format("§f必要レベル: §e%d", getRequiredLevel()),
                String.format("§f値段: §e%d", getPrice())
        };
    }
}
