package com.github.elic0de.thejpspit.message;

import com.github.elic0de.thejpspit.queue.QueueManager;
import com.github.elic0de.thejpspit.queue.ServerQueueType;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class PluginMessageReceiver implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
        ByteArrayDataInput input = ByteStreams.newDataInput(message);

        String server = input.readUTF(); // Name of server, as given in the arguments
        int playercount = input.readInt();

        //QueueManager.update(ServerQueueType.valueOf(server), playercount);
    }
}
