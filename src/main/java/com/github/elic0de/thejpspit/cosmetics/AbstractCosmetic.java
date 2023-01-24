package com.github.elic0de.thejpspit.cosmetics;

import org.bukkit.Material;

public interface AbstractCosmetic {

    // todo: slot charの追加, material
    String getId();

    String getName();

    String getDescription();

    Character getSlot();

    Material getMaterial();

    double getCoin();
}
