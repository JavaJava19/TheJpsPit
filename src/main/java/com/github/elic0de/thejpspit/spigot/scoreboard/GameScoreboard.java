package com.github.elic0de.thejpspit.spigot.scoreboard;

import com.github.elic0de.thejpspit.spigot.TheJpsPit;
import com.github.elic0de.thejpspit.spigot.hook.EconomyHook;
import com.github.elic0de.thejpspit.spigot.leveler.Levels;
import com.github.elic0de.thejpspit.spigot.player.PitPlayer;
import com.github.elic0de.thejpspit.spigot.player.PitPlayerManager;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class GameScoreboard {

    public void update() {
        for (PitPlayer player : Bukkit.getOnlinePlayers().stream()
            .map(PitPlayerManager::getPitPlayer).toList()) {
            player.getBoard().updateLines(
                boardLines(player).stream().map(s -> ChatColor.translateAlternateColorCodes('&', s))
                    .collect(Collectors.toList()));
        }
    }

    public List<String> boardLines(PitPlayer player) {
        final Optional<EconomyHook> optionalHook = TheJpsPit.getInstance().getEconomyHook();
        String coin = "";
        if (optionalHook.isPresent()) {
            final EconomyHook economy = optionalHook.get();

            coin = String.valueOf( economy.getBalance(player).intValue());
        }

        String finalCoin = coin;
        return TheJpsPit.getInstance().getSettings().getScoreboard().stream().map(s ->
            s.replaceAll("%level%",   Levels.getPlayerLevelColor(player.getLevel()) + "" + player.getLevel() + ChatColor.RESET)
                .replaceAll("%neededXp%", Levels.getPlayerNeededXP(player.getLevel(),
                    (int) player.getXp()) + "")
                .replaceAll("%rating%", player.getRating() + "%")
                .replaceAll("%bestRating%", player.getBestRating() + "%")
                .replaceAll("%streaks%", player.getStreaks() + "")
                .replaceAll("%bestStreaks%", player.getBestStreaks() + "")
                .replaceAll("%coins%", finalCoin)
        ).collect(Collectors.toList());
    }

}
