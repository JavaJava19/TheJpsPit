package com.github.elic0de.thejpspit.util;

import com.github.elic0de.thejpspit.player.PitPlayer;
import com.github.elic0de.thejpspit.player.PitPlayerManager;
import com.github.elic0de.thejpspit.TheJpsPit;
import java.math.BigDecimal;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.lang.Math.round;

public class KillAssistHelper implements Listener {

    private final HashMap<UUID, HashMap<UUID, Double>> pitPlayers = new HashMap<>();

    public KillAssistHelper() {
        Bukkit.getPluginManager().registerEvents(this, TheJpsPit.getInstance());
    }

    public void death(PitPlayer victim) {
        if (pitPlayers.containsKey(victim.getUniqueId())) {
            final Map<UUID, Double> damagerMap = Collections.unmodifiableMap(pitPlayers.get(victim.getUniqueId()));
            double totalDamage = 0D;
            for (Double damage : damagerMap.values()) {
                totalDamage += damage;
            }
            for (UUID uuid : damagerMap.keySet()) {
                final Player player = Bukkit.getPlayer(uuid);
                if (player == null) continue;
                final PitPlayer damager = PitPlayerManager.getPitPlayer(player);
                if (victim.getName().equalsIgnoreCase(damager.getName())) continue;
                if (damager.getName().equalsIgnoreCase(victim.getKiller().getName() == null ? "" : victim.getKiller().getName())) continue;
                final double damaged = damagerMap.get(uuid);
                final int assistPer = (int) round(damaged / totalDamage * 100);
                damager.sendMessage("アシストキル [%per%%] %killedPlayer%".replaceAll("%per%", assistPer + "").replaceAll("%killedPlayer%", victim.getName()));
                damager.giveCoin(BigDecimal.valueOf(10));
            }
            pitPlayers.get(victim.getUniqueId()).clear();
        }
    }

    @EventHandler
    private void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player vitim) {
            if (event.getDamager() instanceof Player damager) {
                final PitPlayer victimPitPlayer = PitPlayerManager.getPitPlayer(vitim);
                final PitPlayer pitPlayer = PitPlayerManager.getPitPlayer(damager);

                if (pitPlayers.containsKey(victimPitPlayer.getUniqueId())) {
                    final HashMap<UUID, Double> damages = pitPlayers.get(victimPitPlayer.getUniqueId());
                    final double damage = damages.getOrDefault(pitPlayer.getPlayer().getUniqueId(), 0D);
                    final double totalDamage = damage + event.getDamage();
                    damages.put(pitPlayer.getUniqueId(), totalDamage);
                    return;
                }
                final HashMap<UUID, Double> damages = new HashMap<>();

                damages.put(pitPlayer.getUniqueId(), event.getDamage());
                pitPlayers.put(victimPitPlayer.getUniqueId(), damages);
            }
        }
    }
}
