package com.github.elic0de.thejpspit.cosmetics.impl.trail;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.cosmetics.Cosmetic;
import com.github.elic0de.thejpspit.cosmetics.CosmeticData;
import com.github.elic0de.thejpspit.cosmetics.type.TrailCosmetic;
import com.github.elic0de.thejpspit.player.PitPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.xenondevs.particle.ParticleEffect;

@CosmeticData(id = "greenstar", name = "Green Star Trail", description = "", icon = Material.EMERALD, coin = 50)
public class GreenStar extends Cosmetic implements TrailCosmetic {

    @Override
    public void onTrail(PitPlayer player, Entity target) {
        if (canExecute(player)) {
            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    if (target == null || target.isOnGround() || target.isDead()) {
                        cancel();
                    }
                    ParticleEffect.VILLAGER_HAPPY.display(target.getLocation());
                }
            };
            runnable.runTaskTimer(TheJpsPit.getInstance(), 2, 2);
        }
    }
}