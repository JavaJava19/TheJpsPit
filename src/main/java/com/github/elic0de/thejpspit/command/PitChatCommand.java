package com.github.elic0de.thejpspit.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.gui.PitChatMenu;
import com.github.elic0de.thejpspit.player.PitPlayer;
import com.github.elic0de.thejpspit.player.PitPlayerManager;
import org.bukkit.entity.Player;

public class PitChatCommand extends BaseCommand {
    private final TheJpsPit pit = TheJpsPit.getInstance();

    @CommandAlias("pitchat")
    public void onChat(Player player) {
        final PitPlayer pitPlayer = PitPlayerManager.getPitPlayer(player);
        PitChatMenu.create(pit, "PitChat").show(pitPlayer);
    }
}
