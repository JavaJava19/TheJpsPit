package com.github.elic0de.thejpspit.cosmetics.impl.kill;

import com.github.elic0de.thejpspit.cosmetics.Cosmetic;
import com.github.elic0de.thejpspit.cosmetics.CosmeticData;
import com.github.elic0de.thejpspit.cosmetics.type.KillCosmetic;
import com.github.elic0de.thejpspit.player.PitPlayer;
import org.bukkit.Effect;
import org.bukkit.Material;

@CosmeticData(id = "bloodkill", name = "Blood Kill", description = "BloodのKill Effect", icon = Material.RED_DYE, coin = 50)
public class BloodKillCosmetic extends Cosmetic implements KillCosmetic {

    @Override
    public void onKill(PitPlayer player, PitPlayer target) {
        if (canExecute(player)) {
            player.getPlayer().playEffect(player.getPlayer().getLocation().clone().add(0, 0.5, 0), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
            player.getPlayer().playEffect(player.getPlayer().getLocation().clone().add(0, 1, 0), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
        }
    }
}