package com.github.elic0de.thejpspit.leveler;

public class Level {

    public final int lev;
    public final int neededXP;

    public Level(int lev, int neededXP) {
        this.lev = lev;
        this.neededXP = neededXP;
    }

    public int getLevel() {
        return lev;
    }

    public int getNeededXP() {
        return neededXP;
    }
}
