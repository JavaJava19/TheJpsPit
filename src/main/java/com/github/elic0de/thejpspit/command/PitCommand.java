package com.github.elic0de.thejpspit.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.player.PitPlayer;
import com.github.elic0de.thejpspit.player.PitPlayerManager;
import org.bukkit.entity.Player;

@CommandAlias("pit|tjp|p")
public class PitCommand extends BaseCommand {

    private final TheJpsPit pit = TheJpsPit.getInstance();

    @Subcommand("data")
    @CommandPermission("tjp.data")
    public void onCommand(Player player) {
        final PitPlayer pitPlayer = PitPlayerManager.getPitPlayer(player);

        pitPlayer.sendStatus();
    }

    @Subcommand("reset")
    @CommandPermission("tjp.reset")
    public void onReset(Player player) {
        pit.getDatabase().deletePlayerData();
    }
}
