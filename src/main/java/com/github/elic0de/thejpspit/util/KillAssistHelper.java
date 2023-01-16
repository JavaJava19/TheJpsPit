package com.github.elic0de.thejpspit.util;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.player.PitPlayer;
import com.github.elic0de.thejpspit.player.PitPlayerManager;
import de.themoep.minedown.MineDown;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import static java.lang.Math.round;

public class KillAssistHelper implements Listener {

    private HashMap<UUID, HashMap<UUID, Double>> pitPlayers = new HashMap<>();

    public KillAssistHelper() {
        Bukkit.getPluginManager().registerEvents(this, TheJpsPit.getInstance());
    }

    public void death(PitPlayer pitPlayer) {
        if (pitPlayers.containsKey(pitPlayer.getUniqueId())) {
            final String KillerName = pitPlayer.getKiller().getName();
            double totalDamage = 0D;
            for (Double damage : pitPlayers.get(pitPlayer.getUniqueId()).values()) {
                totalDamage += damage;
            }
            for (UUID uuid : pitPlayers.get(pitPlayer.getUniqueId()).keySet()) {
                final Player player = Bukkit.getPlayer(uuid);
                if (player == null) continue;
                final double damaged = pitPlayers.get(pitPlayer.getUniqueId()).get(uuid);
                final double assistPer = round(damaged/totalDamage * 100);
                player.spigot().sendMessage(new MineDown("アシストキル [%per%]% %killedPlayer%".replaceAll("%per%", assistPer + "").replaceAll("%killedPlayer%", KillerName)).toComponent());
            }
            pitPlayers.get(pitPlayer.getUniqueId()).clear();
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
                    final double damage = damages.getOrDefault(pitPlayer.getPlayer(),0D);
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
