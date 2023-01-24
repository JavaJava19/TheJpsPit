package com.github.elic0de.thejpspit.cosmetics;

import com.github.elic0de.thejpspit.cosmetics.impl.kill.FireCosmetic;
import com.github.elic0de.thejpspit.cosmetics.type.KillCosmetic;
import com.github.elic0de.thejpspit.player.PitPlayer;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableClassToInstanceMap;

public class CosmeticManager {

    ClassToInstanceMap<KillCosmetic> killCosmetics;

    public CosmeticManager() {
        killCosmetics = new ImmutableClassToInstanceMap.Builder<KillCosmetic>()
            .put(FireCosmetic.class, new FireCosmetic())
            .build();
    }

    public void onProcess(PitPlayer player) {
        for (KillCosmetic cosmetic : killCosmetics.values()) {
            cosmetic.onKill(player);
        }
    }

    public ClassToInstanceMap<KillCosmetic> getKillCosmetics() {
        return killCosmetics;
    }
}
