package com.github.elic0de.thejpspit.cosmetics.impl.kill;

import com.github.elic0de.thejpspit.cosmetics.Cosmetic;
import com.github.elic0de.thejpspit.cosmetics.CosmeticData;
import com.github.elic0de.thejpspit.cosmetics.type.KillCosmetic;
import com.github.elic0de.thejpspit.player.PitPlayer;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;

@CosmeticData(id = "Fire", name = "ファイやー", coin = 50)
public class FireCosmetic extends Cosmetic implements KillCosmetic {

    @Override
    public void onKill(PitPlayer player) {
        if (canExecute(player)) {
            new ParticleBuilder(ParticleEffect.FLAME, player.getPlayer().getLocation())
                .setOffsetY(1f)
                .setSpeed(0.1f)
                .display();
        }
    }
}
