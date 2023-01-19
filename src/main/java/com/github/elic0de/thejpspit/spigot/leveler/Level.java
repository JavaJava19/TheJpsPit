package com.github.elic0de.thejpspit.spigot.leveler;

import org.bukkit.ChatColor;

public class Level {

    private final int lev;
    private final int neededXP;

    private final ChatColor levelColor;

    public Level(int lev, int neededXP, ChatColor color) {
        this.lev = lev;
        this.neededXP = neededXP;
        this.levelColor = color;
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
        return neededXP;
    }

    public ChatColor getLevelColor() {
        return levelColor;
    }
}
