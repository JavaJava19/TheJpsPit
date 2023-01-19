package com.github.elic0de.thejpspit.spigot.item;

import com.github.elic0de.thejpspit.spigot.TheJpsPit;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
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

    public static boolean isPitItem(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return false;
        } else {
            return itemMeta.getPersistentDataContainer().get(PitItemEntry.itemIdKey, PersistentDataType.STRING) != null;
        }
    }
}
