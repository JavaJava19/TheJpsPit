package com.github.elic0de.thejpspit.listener;

import com.github.elic0de.thejpspit.TheJpsPit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class CombatTagger implements Listener {

    private static final Map<UUID, Boolean> TAGGED = new HashMap<>();
    private static final Map<UUID, BukkitRunnable> UNTAGGERS = new HashMap<>();
    private final int delay = 15;

    public CombatTagger() {
        Bukkit.getPluginManager().registerEvents(this, TheJpsPit.getInstance());
    }

    public static boolean isTagged(final UUID uuid) {
        boolean result = false;
        final Boolean state = TAGGED.get(uuid);
        if (state != null) {
            result = state;
        }
        return result;
    }

    @EventHandler
    public void onDamage(final EntityDamageByEntityEvent event) {
        final ArrayList<UUID> ids = new ArrayList<>();
        if (event.getEntity() instanceof Player) {
            ids.add(event.getEntity().getUniqueId());
        }
        if (event.getDamager() instanceof Player) {
            ids.add(event.getDamager().getUniqueId());
        }
        for (final UUID uuid : ids) {
            TAGGED.put(uuid, true);
            final BukkitRunnable run = UNTAGGERS.get(uuid);
            if (run != null) {
                run.cancel();
            }
            UNTAGGERS.put(uuid, new BukkitRunnable() {
                @Override
                public void run() {
                    TAGGED.put(uuid, false);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            // TODO コード修正
                            final Map<UUID, Entity> combatPlayers = EventListener.combatPlayers;
                            if (combatPlayers.containsKey(uuid)) {
                                combatPlayers.get(uuid).remove();
                                combatPlayers.remove(uuid);
                            }
                        }
                    }.runTaskLater(TheJpsPit.getInstance(), delay * 20L);
                }
            });
            UNTAGGERS.get(uuid).runTaskLater(TheJpsPit.getInstance(), delay * 20L);
        }
    }

    @EventHandler
    public void onDeath(final PlayerDeathEvent event) {
        final UUID uuid = event.getEntity().getUniqueId();
        TAGGED.remove(uuid);
        final BukkitRunnable runnable = UNTAGGERS.remove(uuid);
        if (runnable != null) {
            runnable.cancel();
        }
    }
}
