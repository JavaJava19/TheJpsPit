package com.github.elic0de.thejpspit.listener;

import com.github.elic0de.thejpspit.TheJpsPit;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

import java.util.HashMap;

public class BlockPlaceListener implements Listener {

    private final TheJpsPit pit;

    private static final HashMap<Location, BlockState> replacedStates = new HashMap<>();

    public BlockPlaceListener() {
        this.pit = TheJpsPit.getInstance();
        Bukkit.getPluginManager().registerEvents(this, pit);
    }

    @EventHandler
    public void on(BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) return;
        final BlockState replacedState = event.getBlockReplacedState();

        if (replacedStates.containsKey(replacedState.getLocation())) return;
        replacedStates.put(replacedState.getLocation(), replacedState);

        pit.getServer().getScheduler().runTaskLater(pit, runnable -> {
            if (replacedStates.containsKey(replacedState.getLocation())) {
                replacedState.update(true);
                replacedStates.remove(replacedState.getLocation());
            }
        }, (10 * 20));
    }

    @EventHandler
    public void on(PlayerBucketEmptyEvent event) {
        final Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) return;
        final BlockState replacedState = event.getBlock().getState();

        if (replacedStates.containsKey(replacedState.getLocation())) return;
        replacedStates.put(replacedState.getLocation(), replacedState);

        pit.getServer().getScheduler().runTaskLater(pit, runnable -> {
            if (replacedStates.containsKey(replacedState.getLocation())) {
                replacedState.update(true);
                replacedStates.remove(replacedState.getLocation());
            }
        }, (10 * 20));
    }

    @EventHandler
    private void on(BlockFormEvent event) {
        if (event.getNewState().getBlock().getType() == Material.OBSIDIAN) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onLiquidSpread(BlockFromToEvent event) {
        Material block = event.getBlock().getType();

        if (block == Material.LAVA || block == Material.WATER) {
            event.getBlock().setType(Material.AIR);
            event.setCancelled(true);
            return;
        }
    }


    public static void restoreBlocks() {
        replacedStates.values().forEach(b -> b.update(true));
    }
}
