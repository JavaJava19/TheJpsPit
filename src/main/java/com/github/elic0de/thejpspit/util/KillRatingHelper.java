package com.github.elic0de.thejpspit.util;

import static java.lang.Math.round;

import com.github.elic0de.thejpspit.player.OfflinePitPlayer;
import com.github.elic0de.thejpspit.player.PitPlayer;

public class KillRatingHelper {

    // 少数第二で四捨五入
    private static final double roundingFactor = 100;
    private final double defaultRating;

    public KillRatingHelper(double defaultRating) {
        this.defaultRating = defaultRating;
    }

    public void initRating(PitPlayer player) {
        player.setRating(calculateKillingRatio(player));
    }

    public void initRating(OfflinePitPlayer player) {
        player.setRating(calculateKillingRatio(player));
    }

    private double calculateKillingRatio(PitPlayer player) {
        long deaths = player.getDeaths();
        long kills = player.getKills();
        long total = deaths + kills;
        if (deaths >= 0 && kills >= 0 && total > 0) {
            return round((double) kills / deaths * roundingFactor) / roundingFactor;
        } else {
            return defaultRating;
        }
    }

    private double calculateKillingRatio(OfflinePitPlayer player) {
        long deaths = player.getDeaths();
        long kills = player.getKills();
        long total = deaths + kills;
        if (deaths >= 0 && kills >= 0 && total > 0) {
            return round((double) kills / deaths * roundingFactor) / roundingFactor;
        } else {
            return defaultRating;
        }
    }
}
