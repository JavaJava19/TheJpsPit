package com.github.elic0de.thejpspit.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.listener.CombatTagger;
import com.github.elic0de.thejpspit.player.PitPlayer;
import com.github.elic0de.thejpspit.player.PitPlayerManager;
import org.bukkit.entity.Player;

public class SpawnCommand extends BaseCommand {

    private final TheJpsPit pit = TheJpsPit.getInstance();

    @CommandAlias("spawn")
    public void spawn(Player player) {
        final PitPlayer pitPlayer = PitPlayerManager.getPitPlayer(player);
        if (CombatTagger.isTagged(player.getUniqueId())) {
            pitPlayer.sendMessage("&cあなたは戦闘中です");
            return;
        }
        pit.getPitPreferences().ifPresent(pitPreferences -> player.teleport(pitPreferences.getSpawn().orElse(player.getLocation())));
    }
}
