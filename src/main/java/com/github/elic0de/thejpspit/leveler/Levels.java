package com.github.elic0de.thejpspit.leveler;

import com.github.elic0de.thejpspit.player.PitPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class Levels {

    private final static List<Level> LEVELS = new ArrayList<>(60);

    static {
        initialize(
                "1,15",
                "10,30",
                "20,50",
                "30,75",
                "40,125",
                "50,250",
                "60,600",
                "70,800",
                "80,900",
                "90,1000"
        );
    }

    private static void initialize(String... texts) {
        Arrays.stream(texts)
                .map(text -> text.split(","))
                .map(data -> new Level(Integer.parseInt(data[0]), Integer.parseInt(data[1])))
                .forEach(LEVELS::add);
    }

    public static int getPlayerLevel(PitPlayer player) {
        return 0;
    }
    public static int getPlayerNeededXP(PitPlayer player) {
        return 0;
    }
}