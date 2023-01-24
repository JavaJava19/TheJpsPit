package com.github.elic0de.thejpspit.cosmetics.type;

import com.github.elic0de.thejpspit.cosmetics.AbstractCosmetic;
import com.github.elic0de.thejpspit.player.PitPlayer;

public interface DeathCosmetic extends AbstractCosmetic {

    default void onDeath(PitPlayer player) {
    }
}
