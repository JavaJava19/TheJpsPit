package com.github.elic0de.thejpspit.network;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.player.PitPlayer;
import com.github.elic0de.thejpspit.queue.QueueServerType;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class PluginMessageReceiver implements PluginMessageListener {

    public static final String BUNGEE_CHANNEL_ID = "BungeeCord";

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals(BUNGEE_CHANNEL_ID)) {
            return;
        }
        ByteArrayDataInput input = ByteStreams.newDataInput(message);

        String server = input.readUTF(); // Name of server, as given in the arguments
        int playercount = input.readInt();

        TheJpsPit.getInstance().getQueueManager().updateQueue(QueueServerType.valueOf(server), playercount);
    }

    public static void sendServerPlayerCount(String serverName) {
        final ByteArrayDataOutput outputStream = ByteStreams.newDataOutput();

        outputStream.writeUTF("PlayerCount");
        outputStream.writeUTF("AKA");

        Bukkit.getServer().sendPluginMessage(TheJpsPit.getInstance() , BUNGEE_CHANNEL_ID, outputStream.toByteArray());
    }

    public static void changeServer(PitPlayer player, String serverName) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(serverName);
        player.getPlayer().sendPluginMessage(TheJpsPit.getInstance(), PluginMessageReceiver.BUNGEE_CHANNEL_ID, out.toByteArray());
    }

}