package com.github.elic0de.thejpspit.spigot.leveler;

import com.github.elic0de.thejpspit.spigot.player.PitPlayer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.ChatColor;

public class Levels {

    private final static List<Level> LEVELS = new ArrayList<>(10);

    static {
        initialize(
            "1,15," + ChatColor.GRAY.name(),
            "10,30," + ChatColor.BLUE.name(),
            "20,50," + ChatColor.DARK_AQUA.name(),
            "30,75," + ChatColor.DARK_GREEN.name(),
            "40,125," + ChatColor.GREEN.name(),
            "50,250," + ChatColor.YELLOW.name(),
            "60,600," + ChatColor.GOLD.name(),
            "70,800," + ChatColor.RED.name(),
            "80,900," + ChatColor.DARK_RED.name(),
            "90,1000," + ChatColor.AQUA.name()
        );
    }

    private static void initialize(String... texts) {
        Arrays.stream(texts)
            .map(text -> text.split(","))
            .map(data -> new Level(Integer.parseInt(data[0]), Integer.parseInt(data[1]), ChatColor.valueOf(data[2])))
            .forEach(LEVELS::add);

        // TODO
        Level level = LEVELS.get(0);
        for (int i = 0; i < LEVELS.get(LEVELS.size() - 1).getLevel() + 10; i++) {
            if (level == null) {
                continue;
            }
            if (LEVELS.get(level.nextLevel()) != null) {
                continue;
            }
            final int nextLevel = level.nextLevel();
            final int neededXp = level.getNeededXP();

            LEVELS.add(new Level(nextLevel, neededXp));
            level = LEVELS.get(nextLevel);
        }
    }

    public static int getPlayerLevel(PitPlayer player) {
        final List<Integer> requirements = LEVELS.stream().map(Level::getNeededXP)
            .toList();
        int maxLevel = requirements.size();
        for (int i = 0; i < maxLevel; i++) {
            if (player.getXp() < requirements.get(i)) {
                return i;
            }
        }
        return maxLevel;
    }

    public static int getPlayerNeededXP(PitPlayer player) {
        final List<Integer> requirements = LEVELS.stream().map(Level::getNeededXP)
            .toList();
        for (int requirement : requirements) {
            int amountToNextLevel = (int) (requirement - player.getXp());
            if (amountToNextLevel > 0) {
                return amountToNextLevel;
            }
        }
        return 0;
    }

    public static int getLevelNeededXP(PitPlayer player) {
        final List<Integer> requirements = LEVELS.stream().map(Level::getNeededXP)
            .toList();
        for (int requirement : requirements) {
            int amountToNextLevel = (int) (requirement - player.getXp());
            if (amountToNextLevel > 0) {
                return requirement;
            }
        }
        return 0;
    }

    public static ChatColor getPlayerLevelColor(PitPlayer player) {
        final List<Integer> requirements = LEVELS.stream().map(Level::getNeededXP)
            .toList();
        int maxLevel = requirements.size();
        for (int i = 0; i < maxLevel; i++) {
            if (player.getXp() < requirements.get(i)) {
                return LEVELS.get(i).getLevelColor();
            }
        }
        return LEVELS.get(maxLevel).getLevelColor();
    }
}