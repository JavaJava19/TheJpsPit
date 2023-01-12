package com.github.elic0de.thejpspit.listener;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.gui.ServerQueueMenu;
import com.github.elic0de.thejpspit.player.PitPlayer;
import com.github.elic0de.thejpspit.player.PitPlayerManager;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;

public class EventListener implements Listener {

    private final TheJpsPit plugin = TheJpsPit.getInstance();

    public EventListener() {
        Bukkit.getPluginManager().registerEvents(this, TheJpsPit.getInstance());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final Optional<PitPlayer> userData = plugin.getDatabase().getPitPlayer(player);
        if (userData.isEmpty()) {
            plugin.getDatabase().createPitPlayer(player);
            PitPlayerManager.registerUser(new PitPlayer(player));
            return;
        }
        // Update the user's name if it has changed
        final PitPlayer pitPlayer = userData.get();
        boolean updateNeeded = false;

        if (!pitPlayer.getName().equals(player.getName())) {
            updateNeeded = true;
        }

        PitPlayerManager.registerUser(pitPlayer);
        if (updateNeeded) {
            plugin.getDatabase().updateUserData(pitPlayer);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final PitPlayer player = PitPlayerManager.getPitPlayer(event.getPlayer());

        PitPlayerManager.unregisterUser(player);
        plugin.getGame().leave(player);
        if (player.getBoard() != null) {
            player.getBoard().delete();
        }
    }


    @EventHandler
    public void onHungerChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
        event.setFoodLevel(20);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        plugin.getGame().death(PitPlayerManager.getPitPlayer(event.getEntity()));
        event.getDrops().clear();
        event.setDeathMessage(null);
    }

    @EventHandler
    public void onDurability(PlayerItemDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player vitim) {
            if (event.getDamager() instanceof Player damager) {
                final PitPlayer victimPitPlayer = PitPlayerManager.getPitPlayer(vitim);
                final PitPlayer pitPlayer = PitPlayerManager.getPitPlayer(damager);
                if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    event.setCancelled(true);
                    return;
                }
                pitPlayer.showHealth(victimPitPlayer);
            }
        }
    }

    @EventHandler
    private void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onXpGain(PlayerExpChangeEvent event) {
        event.setAmount(0);
    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Action action = event.getAction();
        if (!(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        Player player = event.getPlayer();
        if (player.isSneaking()) {
            return;
        }
        if (event.getItem() == null) {
            return;
        }
        if (event.getItem().getType() != Material.NETHER_STAR) {
            return;
        }

        ServerQueueMenu.create(plugin, "サーバーキュー").show(PitPlayerManager.getPitPlayer(player));

        event.setCancelled(true);
    }
}
