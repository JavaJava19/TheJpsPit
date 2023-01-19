package com.github.elic0de.thejpspit.spigot.network;

import com.github.elic0de.thejpspit.spigot.TheJpsPit;
import com.github.elic0de.thejpspit.spigot.player.PitPlayer;
import com.github.elic0de.thejpspit.spigot.queue.QueueServerType;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class PluginMessageReceiver implements PluginMessageListener {

    public static final String BUNGEE_CHANNEL_ID = "BungeeCord";

    public static void sendServerPlayerCount() {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("TheJpsPit:request");

        Bukkit.getServer().sendPluginMessage(TheJpsPit.getInstance(), BUNGEE_CHANNEL_ID, out.toByteArray());
    }

    public static void changeServer(PitPlayer player, String serverName) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(serverName);
        player.getPlayer()
            .sendPluginMessage(TheJpsPit.getInstance(), PluginMessageReceiver.BUNGEE_CHANNEL_ID,
                out.toByteArray());
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals(BUNGEE_CHANNEL_ID)) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        if (!in.readUTF().equals("TheJpsPit:response")) return;

        HashMap<String, Integer> networkInformation = new HashMap<>();
        int length = in.readInt();
        for (int i = 0; i < length; i++) networkInformation.put(in.readUTF(), in.readInt());

        networkInformation.forEach((s, integer) -> TheJpsPit.getInstance().getQueueManager()
            .updateQueue(s, integer));

    }

}