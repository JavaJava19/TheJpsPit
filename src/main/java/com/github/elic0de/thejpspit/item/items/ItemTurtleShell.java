package com.github.elic0de.thejpspit.item.items;

import com.github.elic0de.thejpspit.item.PitItemEntry;
import com.github.elic0de.thejpspit.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class ItemTurtleShell extends PitItemEntry {

    @Override
    public String getId() {
        return "turtle_shell";
    }

    @Override
    public ItemStack getRawItemStack() {
        return ItemUtil.withUnbreakable(new ItemStack(Material.TURTLE_HELMET));
    }

    @Override
    public int getPrice() {
        return 198000;
    }

    @Override
    public int getRequiredLevel() {
        return 30;
    }

    @Override
    public String getName() {
        return "§6GUCCIのヘルメット";
    }

    @Override
    public char getSlotChar() {
        return 't';
    }

    @Override
    public List<String> getRawLore() {
        return Arrays.asList("§7言わずと知れた高級ブランド", "§7防御力はあまりない");
    }
}
