package com.github.elic0de.thejpspit.cosmetics.impl.death;

import com.github.elic0de.thejpspit.cosmetics.Cosmetic;
import com.github.elic0de.thejpspit.cosmetics.CosmeticData;
import com.github.elic0de.thejpspit.cosmetics.type.DeathCosmetic;
import com.github.elic0de.thejpspit.player.PitPlayer;
import org.bukkit.Material;
import org.bukkit.Sound;

@CosmeticData(id = "cat", name = "Cat", description = "猫の悲鳴", icon = Material.CAT_SPAWN_EGG, coin = 50)
public class CatCosmetic extends Cosmetic implements DeathCosmetic {

    @Override
    public void onDeath(PitPlayer player) {
        if (canExecute(player)) {
            player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.ENTITY_CAT_DEATH, 1F,1F);
        }
    }
}

