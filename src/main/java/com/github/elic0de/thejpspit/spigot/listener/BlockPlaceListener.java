package com.github.elic0de.thejpspit.spigot.listener;

import com.github.elic0de.thejpspit.spigot.TheJpsPit;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

import java.util.HashSet;
import java.util.Set;

public class BlockPlaceListener implements Listener {

    private final TheJpsPit pit;

    private static final Set<BlockState> replacedStates = new HashSet<>();

    public BlockPlaceListener() {
        this.pit = TheJpsPit.getInstance();
        Bukkit.getPluginManager().registerEvents(this, pit);
    }

    @EventHandler
    public void on(BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) return;
        final BlockState replacedState = event.getBlockReplacedState();

        replacedStates.add(replacedState);

        pit.getServer().getScheduler().runTaskLater(pit, runnable -> {
            if (replacedStates.contains(replacedState)) {
                replacedState.update(true);
                replacedStates.remove(replacedState);
            }
        }, (15 * 20));
    }

    @EventHandler
    public void on(PlayerBucketEmptyEvent event) {
        final Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) return;
        final BlockState replacedState = event.getBlock().getState();

        replacedStates.add(replacedState);

        pit.getServer().getScheduler().runTaskLater(pit, runnable -> {
            if (replacedStates.contains(replacedState)) {
                replacedState.update(true);
                replacedStates.remove(replacedState);
            }
        }, (15 * 20));
    }

    public static void restoreBlocks() {
        replacedStates.forEach(b -> b.update(true));
    }
}
