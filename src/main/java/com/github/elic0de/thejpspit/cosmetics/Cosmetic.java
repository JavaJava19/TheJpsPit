package com.github.elic0de.thejpspit.cosmetics;

import com.github.elic0de.thejpspit.player.PitPlayer;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bukkit.Material;

public class Cosmetic implements AbstractCosmetic {

    private String cosmeticId;
    private String cosmeticName;
    private String cosmeticDescription;
    private Character slot;
    private Material material;
    private double coin;

    public Cosmetic() {
        final Class<?> cosmeticClass = this.getClass();

        if (cosmeticClass.isAnnotationPresent(CosmeticData.class)) {
            final CosmeticData cosmeticData = cosmeticClass.getAnnotation(CosmeticData.class);
            this.cosmeticId = cosmeticData.id();
            this.cosmeticName = cosmeticData.name();
            this.cosmeticDescription = cosmeticData.description();
            this.slot = cosmeticData.slot();
            this.material = cosmeticData.icon();
            this.coin = cosmeticData.coin();
        }
    }

    public final boolean canExecute(PitPlayer player) {
        final AtomicBoolean canExecute = new AtomicBoolean(false);
        player.getPreferences().ifPresent(preferences -> canExecute.set(preferences.getCosmeticsCollection().isSelectedCosmetic(this)));

        return canExecute.get();
    }

    public String getId() {
        return cosmeticId;
    }

    public String getName() {
        return cosmeticName;
    }

    public String getDescription() {
        return cosmeticDescription;
    }

    public Character getSlot() {
        return slot;
    }

    public Material getMaterial() {
        return material;
    }

    public double getCoin() {
        return coin;
    }
}