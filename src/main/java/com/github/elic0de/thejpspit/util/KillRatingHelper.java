package com.github.elic0de.thejpspit.util;

import static java.lang.Math.round;

import com.github.elic0de.thejpspit.player.PitPlayer;

public class KillRatingHelper {

    private static final double roundingFactor = 10000;
    private final double defaultRating;

    public KillRatingHelper(double defaultRating) {
        this.defaultRating = defaultRating;
    }

    public void initRating(PitPlayer player) {
        player.setRating(calculateWinningRatio(player));
    }

    private double calculateWinningRatio(PitPlayer player) {
        long deaths = player.getDeaths();
        long kills = player.getKills();
        long total = deaths + kills;
        if (deaths >= 0 && kills >= 0 && total > 0) {
            return round((double) kills / total * roundingFactor) / roundingFactor;
        } else {
            return defaultRating;
        }
    }
}
