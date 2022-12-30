package com.github.elic0de.thejpspit.listener;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.player.PitPlayer;
import com.github.elic0de.thejpspit.player.PitPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener {

    private final TheJpsPit plugin = TheJpsPit.getInstance();
    public EventListener() {
        Bukkit.getPluginManager().registerEvents(this, TheJpsPit.getInstance());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.getDatabase().ensureUser(PitPlayer.adapt(event.getPlayer())).thenRun(() ->
                plugin.getDatabase().getPitPlayer(event.getPlayer()).join().ifPresent(pitPlayer -> {
                    if (!PitPlayerManager.isContain(event.getPlayer())) PitPlayerManager.registerUser(pitPlayer);
                    pitPlayer.showHealth();
                    plugin.getGame().join(pitPlayer);
                })
        );
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final PitPlayer player = PitPlayerManager.getPitPlayer(event.getPlayer());

        plugin.getGame().leave(player);
        player.getBoard().delete();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        event.setCancelled(true);
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
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            final PitPlayer pitPlayer = PitPlayerManager.getPitPlayer(player);
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                event.setCancelled(true);
                return;
            }

            pitPlayer.showHealth();
        }
    }

    @EventHandler
    private void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) event.setCancelled(true);
    }
}
