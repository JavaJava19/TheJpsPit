package com.github.elic0de.thejpspit.spigot.item;

import com.github.elic0de.thejpspit.spigot.TheJpsPit;
import com.github.elic0de.thejpspit.spigot.item.items.*;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;

public class ItemManager {

    private static final HashMap<String, PitItemEntry> pitItemMap = new HashMap<>();

    public ItemManager() {
    }

    public static void register(PitItemEntry pitItem) {
        pitItemMap.put(pitItem.getId(), pitItem);
        if (pitItem instanceof Listener listener) {
            TheJpsPit.getInstance().getServer().getPluginManager().registerEvents(listener, TheJpsPit.getInstance());
        }
    }

    @Nullable
    public static PitItemEntry getPitItemEntry(String id) {
        return pitItemMap.get(id);
    }

    @Nullable
    public static PitItemEntry getPitItemEntry(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        return itemMeta == null ? null : getPitItemEntry(itemMeta.getPersistentDataContainer().get(PitItemEntry.itemIdKey, PersistentDataType.STRING));
    }

    public static Collection<PitItemEntry> getAllEntry() {
        return pitItemMap.values();
    }

    public static boolean isPitItem(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return false;
        } else {
            return itemMeta.getPersistentDataContainer().get(PitItemEntry.itemIdKey, PersistentDataType.STRING) != null;
        }
    }

    public static void createItems() {
        ItemManager.register(new ItemDiamondSword());
        ItemManager.register(new ItemDiamondChestPlate());
        ItemManager.register(new ItemDiamondBoots());
        ItemManager.register(new ItemObsidian());
        ItemManager.register(new ItemVividSword());
        ItemManager.register(new ItemCobweb());
        ItemManager.register(new ItemFishingRod());
        ItemManager.register(new ItemTurtleShell());
        ItemManager.register(new ItemUltimateSword());
        ItemManager.register(new ItemLavaBucket());
    }
}
