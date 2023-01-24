package com.github.elic0de.thejpspit.cosmetics.impl.death;

import com.github.elic0de.thejpspit.cosmetics.Cosmetic;
import com.github.elic0de.thejpspit.cosmetics.CosmeticData;
import com.github.elic0de.thejpspit.cosmetics.type.DeathCosmetic;
import com.github.elic0de.thejpspit.player.PitPlayer;
import org.bukkit.Material;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;

@CosmeticData(id = "blooddeath", name = "床が赤でいっぱい", description = "ち", icon = Material.REDSTONE, coin = 50)
public class BloodDeathCosmetic extends Cosmetic implements DeathCosmetic {

    @Override
    public void onDeath(PitPlayer player) {
        if (canExecute(player)) {
            new ParticleBuilder(ParticleEffect.REDSTONE, player.getPlayer().getLocation())
                .setAmount(15)
                .setOffsetY(1f)
                .setSpeed(0.1f)
                .display();        }
    }
}