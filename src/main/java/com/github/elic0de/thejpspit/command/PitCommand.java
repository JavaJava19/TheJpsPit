package com.github.elic0de.thejpspit.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.player.PitPlayer;
import com.github.elic0de.thejpspit.player.PitPlayerManager;
import com.github.elic0de.thejpspit.villager.VillagerNPCManager;
import org.bukkit.entity.Player;

@CommandAlias("pit|tjp|p")
public class PitCommand extends BaseCommand {

    private final TheJpsPit pit = TheJpsPit.getInstance();

    @Subcommand("data")
    @CommandPermission("tjp.data")
    public void onCommand(Player player, @Optional OnlinePlayer onlinePlayer) {
        final PitPlayer pitPlayer = PitPlayerManager.getPitPlayer(player);

        if (onlinePlayer == null) {
            pitPlayer.sendStatus();
            return;
        }

        pitPlayer.sendStatus(PitPlayerManager.getPitPlayer(onlinePlayer.getPlayer()));
    }

    @Subcommand("reset")
    @CommandPermission("tjp.reset")
    public void onReset(Player player) {
        pit.getDatabase().deletePlayerData();
    }

    @Subcommand("set spawn")
    @CommandPermission("tjp.spawn")
    public void onSetSpawn(Player player) {
        pit.getPitPreferences().ifPresent(pitPreferences -> pitPreferences.setSpawn(player.getLocation()));
    }

    @Subcommand("shop")
    @CommandPermission("tjp.shop")
    public void onCreateShop(Player player) {
        VillagerNPCManager.getVillagerNPC("shop").spawnAt(player.getWorld(), player.getLocation());
    }
}
