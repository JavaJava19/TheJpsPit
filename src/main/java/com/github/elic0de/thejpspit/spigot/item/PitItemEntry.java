package com.github.elic0de.thejpspit.spigot.item;

import com.github.elic0de.thejpspit.spigot.TheJpsPit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class PitItemEntry {

    public static final NamespacedKey itemIdKey = new NamespacedKey(TheJpsPit.getInstance(), "itemId");

    public abstract String getId();

    public abstract ItemStack getRawItemStack();

    public abstract int getPrice();

    public abstract int getRequiredLevel();

    public abstract String getName();

    public abstract char getSlotChar();

    public List<String> getRawLore() {
        return new ArrayList<>();
    }

    public final ItemStack getItemStack() {
        ItemStack itemStack = this.getRawItemStack().clone();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.getPersistentDataContainer().set(itemIdKey, PersistentDataType.STRING, this.getId());
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_DYE, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_DESTROYS);
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }

    public String[] getLore() {
        return new ArrayList<String>() {
            {
                add(getName());
                addAll(getRawLore());
                add(String.format("§f必要レベル: §e%d", getRequiredLevel()));
                add(String.format("§f値段: §e%d", getPrice()));
            }
        }.toArray(String[]::new);
    }

    protected boolean isEntryOf(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return false;
        return (Objects.requireNonNullElse(itemMeta.getPersistentDataContainer().get(itemIdKey, PersistentDataType.STRING), "")).equals(this.getId());
    }
}
