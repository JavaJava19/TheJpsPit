package com.github.elic0de.thejpspit.cosmetics;

import com.github.elic0de.thejpspit.cosmetics.impl.death.BloodDeathCosmetic;
import com.github.elic0de.thejpspit.cosmetics.impl.death.CatCosmetic;
import com.github.elic0de.thejpspit.cosmetics.impl.death.LightingDeathCosmetic;
import com.github.elic0de.thejpspit.cosmetics.impl.kill.BloodKillCosmetic;
import com.github.elic0de.thejpspit.cosmetics.impl.kill.FireCosmetic;
import com.github.elic0de.thejpspit.cosmetics.impl.trail.GreenStar;
import com.github.elic0de.thejpspit.cosmetics.type.AuraCosmetic;
import com.github.elic0de.thejpspit.cosmetics.type.DeathCosmetic;
import com.github.elic0de.thejpspit.cosmetics.type.KillCosmetic;
import com.github.elic0de.thejpspit.cosmetics.type.StreakCosmetic;
import com.github.elic0de.thejpspit.cosmetics.type.TrailCosmetic;
import com.github.elic0de.thejpspit.player.PitPlayer;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableClassToInstanceMap;
import org.bukkit.entity.Entity;

public class CosmeticManager {

    ClassToInstanceMap<KillCosmetic> killCosmetics;
    ClassToInstanceMap<DeathCosmetic> deathCosmetics;
    ClassToInstanceMap<StreakCosmetic> streakCosmetics;
    ClassToInstanceMap<TrailCosmetic> trailCosmetics;
    ClassToInstanceMap<AuraCosmetic> auraCosmetics;

    public ClassToInstanceMap<AbstractCosmetic> allCosmetics;

    public CosmeticManager() {
        killCosmetics = new ImmutableClassToInstanceMap.Builder<KillCosmetic>()
            .put(FireCosmetic.class, new FireCosmetic())
            .put(BloodKillCosmetic.class, new BloodKillCosmetic())
            .build();

        deathCosmetics = new ImmutableClassToInstanceMap.Builder<DeathCosmetic>()
            .put(CatCosmetic.class, new CatCosmetic())
            .put(BloodDeathCosmetic.class, new BloodDeathCosmetic())
            .put(LightingDeathCosmetic.class, new LightingDeathCosmetic())
            .build();

        streakCosmetics = new ImmutableClassToInstanceMap.Builder<StreakCosmetic>()
            .build();

        trailCosmetics = new ImmutableClassToInstanceMap.Builder<TrailCosmetic>()
            .put(GreenStar.class, new GreenStar())
            .build();

        auraCosmetics = new ImmutableClassToInstanceMap.Builder<AuraCosmetic>()
            .build();

        allCosmetics = new ImmutableClassToInstanceMap.Builder<AbstractCosmetic>()
            .putAll(killCosmetics)
            .putAll(deathCosmetics)
            .putAll(streakCosmetics)
            .putAll(trailCosmetics)
            .putAll(auraCosmetics)
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

    public void onStreak(PitPlayer player) {
        for (StreakCosmetic cosmetic : streakCosmetics.values()) {
            cosmetic.onStreak(player);
        }
    }

    public void onTrail(PitPlayer player, Entity target) {
        for (TrailCosmetic cosmetic : trailCosmetics.values()) {
            cosmetic.onTrail(player, target);
        }
    }

    public void onAura(PitPlayer player) {
        for (AuraCosmetic cosmetic : auraCosmetics.values()) {
            cosmetic.onAura(player);
        }
    }

    public ClassToInstanceMap<KillCosmetic> getKillCosmetics() {
        return killCosmetics;
    }

    public ClassToInstanceMap<DeathCosmetic> getDeathCosmetics() {
        return deathCosmetics;
    }

    public ClassToInstanceMap<StreakCosmetic> getStreakCosmetics() {
        return streakCosmetics;
    }

    public ClassToInstanceMap<TrailCosmetic> getTrailCosmetics() {
        return trailCosmetics;
    }

    public ClassToInstanceMap<AuraCosmetic> getAuraCosmetics() {
        return auraCosmetics;
    }

    public ClassToInstanceMap<AbstractCosmetic> getAllCosmetics() {
        return allCosmetics;
    }
}
