package com.github.elic0de.thejpspit.spigot.item;

import com.github.elic0de.thejpspit.spigot.TheJpsPit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public abstract class PitItemEntry {

    public static final NamespacedKey itemIdKey = new NamespacedKey(TheJpsPit.getInstance(), "itemId");

    public abstract String getId();

    public abstract ItemStack getRawItemStack();

    public abstract int getPrice();

    public abstract int getRequiredLevel();

    public abstract String getName();

    public final ItemStack getItemStack() {
        ItemStack itemStack = this.getRawItemStack().clone();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.getPersistentDataContainer().set(itemIdKey, PersistentDataType.STRING, this.getId());
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }

    public String[] getLore() {
        return new String[]{
                getName(),
                String.format("必要レベル: §e%d", this.getRequiredLevel()),
                String.format("値段: §e%d", this.getPrice())
        };
    }

    protected boolean isEntryOf(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return false;
        return (Objects.requireNonNullElse(itemMeta.getPersistentDataContainer().get(itemIdKey, PersistentDataType.STRING), "")).equals(this.getId());
    }
}
