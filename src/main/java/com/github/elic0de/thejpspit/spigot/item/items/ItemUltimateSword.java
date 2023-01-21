package com.github.elic0de.thejpspit.spigot.item.items;

import com.github.elic0de.thejpspit.spigot.item.PitItemEntry;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ItemUltimateSword extends PitItemEntry {

    @Override
    public String getId() {
        return "ultimate_sword";
    }

    @Override
    public ItemStack getRawItemStack() {
        ItemStack itemStack = new ItemStack(Material.WOODEN_SWORD);
        ItemMeta itemMeta = Objects.requireNonNull(itemStack.getItemMeta());
        itemMeta.setUnbreakable(true);
        itemMeta.setDisplayName("§e§lつよつよ剣");
        itemMeta.addEnchant(Enchantment.KNOCKBACK, 10, true);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @Override
    public int getPrice() {
        return 100000;
    }

    @Override
    public int getRequiredLevel() {
        return 5;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public char getSlotChar() {
        return 'u';
    }

    @Override
    public List<String> getRawLore() {
        return Arrays.asList("§7ノックバック 10");
    }
}
