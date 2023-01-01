package com.github.elic0de.thejpspit.leveler;

public class Level {

    private final int lev;
    private final int neededXP;

    public Level(int lev, int neededXP) {
        this.lev = lev;
        this.neededXP = neededXP;
    }

    public int nextLevel() {
        return lev + 1;
    }

    public int previousLevel() {
        return Math.max(lev - 1, 0);
    }

    public int getLevel() {
        return lev;
    }

    public int getNeededXP() {
        return neededXP * lev;
    }
}
