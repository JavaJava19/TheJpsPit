package com.github.elic0de.thejpspit.cosmetics;

import com.github.elic0de.thejpspit.cosmetics.impl.death.BloodDeathCosmetic;
import com.github.elic0de.thejpspit.cosmetics.impl.death.CatCosmetic;
import com.github.elic0de.thejpspit.cosmetics.impl.kill.BloodKillCosmetic;
import com.github.elic0de.thejpspit.cosmetics.impl.kill.FireCosmetic;
import com.github.elic0de.thejpspit.cosmetics.type.DeathCosmetic;
import com.github.elic0de.thejpspit.cosmetics.type.KillCosmetic;
import com.github.elic0de.thejpspit.player.PitPlayer;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableClassToInstanceMap;

public class CosmeticManager {

    ClassToInstanceMap<KillCosmetic> killCosmetics;
    ClassToInstanceMap<DeathCosmetic> deathCosmetics;

    public ClassToInstanceMap<AbstractCosmetic> allCosmetics;

    public CosmeticManager() {
        killCosmetics = new ImmutableClassToInstanceMap.Builder<KillCosmetic>()
            .put(FireCosmetic.class, new FireCosmetic())
            .put(BloodKillCosmetic.class, new BloodKillCosmetic())
            .build();

        deathCosmetics = new ImmutableClassToInstanceMap.Builder<DeathCosmetic>()
            .put(CatCosmetic.class, new CatCosmetic())
            .put(BloodDeathCosmetic.class, new BloodDeathCosmetic())
            .build();

        allCosmetics = new ImmutableClassToInstanceMap.Builder<AbstractCosmetic>()
            .putAll(killCosmetics)
            .putAll(deathCosmetics)
            .build();
    }

    public void onKill(PitPlayer player, PitPlayer target) {
        for (KillCosmetic cosmetic : killCosmetics.values()) {
            cosmetic.onKill(player, target);
        }
    }

    public void onDeath(PitPlayer player) {
        for (DeathCosmetic cosmetic : deathCosmetics.values()) {
            cosmetic.onDeath(player);
        }
    }

    public ClassToInstanceMap<KillCosmetic> getKillCosmetics() {
        return killCosmetics;
    }

    public ClassToInstanceMap<DeathCosmetic> getDeathCosmetics() {
        return deathCosmetics;
    }

    public ClassToInstanceMap<AbstractCosmetic> getAllCosmetics() {
        return allCosmetics;
    }
}
