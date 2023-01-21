package com.github.elic0de.thejpspit.spigot.item.items;

import com.github.elic0de.thejpspit.spigot.item.PitItemEntry;
import com.github.elic0de.thejpspit.spigot.player.PitPlayer;
import com.github.elic0de.thejpspit.spigot.player.PitPlayerManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class ItemVividSword extends PitItemEntry implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void on(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        PitPlayer killer = PitPlayerManager.getPitPlayer(victim).getKiller();
        if (killer != null) {
            if (this.isEntryOf(killer.getPlayer().getInventory().getItemInMainHand())) {
                victim.getWorld().strikeLightningEffect(victim.getLocation());
            }

        }
    }

    @Override
    public String getId() {
        return "vivid_sword";
    }

    @Override
    public ItemStack getRawItemStack() {
        ItemStack itemStack = new ItemStack(Material.IRON_SWORD);
        ItemMeta itemMeta = Objects.requireNonNull(itemStack.getItemMeta());
        itemMeta.setUnbreakable(true);
        itemMeta.setDisplayName("§e§lかっこいい剣");
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @Override
    public int getPrice() {
        return 100000;
    }

    @Override
    public int getRequiredLevel() {
        return 0;
    }

    @Override
    public String getName() {
        return "§aかっこいい剣";
    }

    @Override
    public char getSlotChar() {
        return 'v';
    }
}
