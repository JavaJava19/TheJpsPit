package com.github.elic0de.thejpspit.leveler;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.player.PitPlayer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.bukkit.ChatColor;

public class Levels {

    private final static HashMap<Integer,Level> LEVELS = new HashMap<>();

    static {
        initialize();
    }
    public static void initialize() {
        Arrays.stream(TheJpsPit.getInstance().getSettings().getLevel().toArray(new String[0]))
            .map(text -> text.split(","))
            .map(data -> new Level(Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]), ChatColor.valueOf(data[3])))
            .forEach(level -> LEVELS.put(level.getLevel(), level));

        final HashMap<Integer,Level> addedLevels = new HashMap<>();
        for (Level level : LEVELS.values()) {
            final int l = level.getLevel();
            final int neededXp = level.getNeededXP();
            final ChatColor color = level.getLevelColor();
            int totalXp = level.getTotalXp();
            for (int i = 1; i < 10; i++){
                final int nextLevel = l + i;
                if (LEVELS.containsKey(nextLevel)) continue;
                totalXp += neededXp;
                addedLevels.put(nextLevel, new Level(nextLevel, neededXp, totalXp, color));
            }
        }
        LEVELS.putAll(addedLevels);
    }

    public static int getPlayerLevel(PitPlayer player) {
        final List<Integer> requirements = LEVELS.values().stream().map(Level::getTotalXp)
            .toList();
        int maxLevel = requirements.size();
        for (int i = 0; i < maxLevel; i++) {
            if (player.getXp() < requirements.get(i)) {
                return i + 1;
            }
        }
        return maxLevel;
    }

    public static int getPlayerNeededXP(int playerLevel, int currentXp) {
        if (LEVELS.containsKey(playerLevel)) {
            final Level level = LEVELS.get(playerLevel);
            final int requirementXp = level.getTotalXp() - currentXp;
            if (requirementXp > 0) {
                return requirementXp;
            }
        }
        return 0;
    }

    public static int getLevelTotalXP(int playerLevel) {
        if (LEVELS.containsKey(playerLevel)) {
            final Level level = LEVELS.get(playerLevel);
            return level.getTotalXp();
        }
        return 0;
    }

    public static ChatColor getPlayerLevelColor(int playerLevel) {
        if (LEVELS.containsKey(playerLevel)) {
            final Level level = LEVELS.get(playerLevel);
            return level.getLevelColor();
        }
        return ChatColor.GRAY;
    }
}