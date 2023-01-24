package com.github.elic0de.thejpspit.cosmetics.impl.death;

import com.github.elic0de.thejpspit.cosmetics.Cosmetic;
import com.github.elic0de.thejpspit.cosmetics.CosmeticData;
import com.github.elic0de.thejpspit.cosmetics.type.DeathCosmetic;
import com.github.elic0de.thejpspit.player.PitPlayer;
import org.bukkit.Material;

// todo:多分 typoみす
@CosmeticData(id = "light", name = "雷", description = "うるさい", icon = Material.WATER, coin = 50)
public class LightingDeathCosmetic extends Cosmetic implements DeathCosmetic {

    @Override
    public void onDeath(PitPlayer player) {
        if (canExecute(player)) {
            player.getPlayer().getLocation().getWorld().strikeLightningEffect(player.getPlayer().getLocation());
        }
    }
}
