package com.github.elic0de.thejpspit.listener;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.gui.ServerQueueMenu;
import com.github.elic0de.thejpspit.player.PitPlayer;
import com.github.elic0de.thejpspit.player.PitPlayerManager;
import fr.mrmicky.fastboard.FastBoard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;

public class EventListener implements Listener {

    private final TheJpsPit plugin = TheJpsPit.getInstance();
    public EventListener() {
        Bukkit.getPluginManager().registerEvents(this, TheJpsPit.getInstance());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (PitPlayerManager.isContain(event.getPlayer())) {
            plugin.getGame().join(PitPlayerManager.getPitPlayer(event.getPlayer()));
            return;
        }
        plugin.getDatabase().ensureUser(event.getPlayer()).thenRun(() ->
                plugin.getDatabase().getPitPlayer(event.getPlayer()).join().ifPresent(pitPlayer -> {
                    PitPlayerManager.registerUser(pitPlayer);
                    plugin.getGame().join(pitPlayer);
                })
        );
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final PitPlayer player = PitPlayerManager.getPitPlayer(event.getPlayer());

        PitPlayerManager.unregisterUser(player);
        plugin.getGame().leave(player);
        if (player.getBoard() != null) player.getBoard().delete();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        PitPlayerManager.getPitPlayer(event.getPlayer()).increaseXP();
        event.setCancelled(true);
    }

    @EventHandler
    public void onHungerChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
        event.setFoodLevel(20);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;
        plugin.getGame().death(PitPlayerManager.getPitPlayer(event.getEntity()));
        event.getDrops().clear();
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
                return;
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player vitim) {
            if (event.getDamager() instanceof Player) {
                final PitPlayer pitPlayer = PitPlayerManager.getPitPlayer(vitim);
                if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    event.setCancelled(true);
                    return;
                }
                pitPlayer.showHealth(pitPlayer);
            }
        }
    }

    @EventHandler
    private void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) event.setCancelled(true);
    }

    @EventHandler
    public void onXpGain(PlayerExpChangeEvent event) {
        event.setAmount(0);
    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        Action action = event.getAction();
        if (!(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) return;

        Player player = event.getPlayer();
        if (player.isSneaking()) return;

        if (event.getItem().getType() != Material.NETHER_STAR) return;

        ServerQueueMenu.create(plugin, "サーバーキュー").show(PitPlayerManager.getPitPlayer(player));

        event.setCancelled(true);
    }
}
