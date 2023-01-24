package com.github.elic0de.thejpspit.cosmetics.type;

import com.github.elic0de.thejpspit.cosmetics.AbstractCosmetic;
import com.github.elic0de.thejpspit.player.PitPlayer;

public interface StreakCosmetic extends AbstractCosmetic {

    default void onStreak(PitPlayer player) {
    }
}