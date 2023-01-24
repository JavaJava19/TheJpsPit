package com.github.elic0de.thejpspit.cosmetics.impl.kill;

import com.github.elic0de.thejpspit.cosmetics.Cosmetic;
import com.github.elic0de.thejpspit.cosmetics.CosmeticData;
import com.github.elic0de.thejpspit.cosmetics.type.KillCosmetic;
import com.github.elic0de.thejpspit.player.PitPlayer;
import org.bukkit.Material;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;

@CosmeticData(id = "bloodkill", name = "あか", description = "あかかかかｋ", icon = Material.RED_DYE, coin = 50)
public class BloodKillCosmetic extends Cosmetic implements KillCosmetic {

    @Override
    public void onKill(PitPlayer player, PitPlayer target) {
        if (canExecute(player)) {
            new ParticleBuilder(ParticleEffect.REDSTONE, target.getPlayer().getLocation())
                .setAmount(15)
                .setOffsetY(1f)
                .setSpeed(0.1f)
                .display();
        }
    }
}