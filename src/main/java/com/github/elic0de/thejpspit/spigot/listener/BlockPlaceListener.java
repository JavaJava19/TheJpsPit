package com.github.elic0de.thejpspit.spigot.listener;

import com.github.elic0de.thejpspit.spigot.TheJpsPit;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {

    private final TheJpsPit pit;

    private static final Set<Location> water = new HashSet<>();
    private static final Set<Location> blocksPlaced = new HashSet<>();

    public BlockPlaceListener() {
        this.pit = TheJpsPit.getInstance();
        Bukkit.getPluginManager().registerEvents(this, pit);
    }

    @EventHandler
    public void on(BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) return;
        final Block placedBlock = event.getBlockPlaced();
        final Location placedBlockLocation = placedBlock.getLocation();

        if(event.getBlockReplacedState().getType() == Material.WATER) water.add(placedBlock.getLocation());

        blocksPlaced.add(placedBlockLocation);

        pit.getServer().getScheduler().runTaskLater(pit, runnable -> {
            if (water.contains(placedBlockLocation)) {
                placedBlock.setType(Material.WATER);
            } else {
                placedBlock.setType(Material.AIR);
            }
            blocksPlaced.remove(placedBlockLocation);
        }, (15 * 20));
    }

    public static void removeBlocks() {
        for (Location location : blocksPlaced) {
            location.getBlock().setType(Material.AIR);
        }
        for (Location location : water) {
            location.getBlock().setType(Material.WATER);
        }
    }
}
