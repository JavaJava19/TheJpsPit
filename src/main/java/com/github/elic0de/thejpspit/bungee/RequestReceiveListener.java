package com.github.elic0de.thejpspit.bungee;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.util.Collection;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;



public class RequestReceiveListener implements Listener {

    private final TheJpsPit plugin = TheJpsPit.getInstance();

    @EventHandler
    public void on(PluginMessageEvent event) {
        if (!event.getTag().equals("BungeeCord")) return;

        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        if (!in.readUTF().equals("TheJpsPit:request")) return;

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("TheJpsPit:response");

        Collection<ServerInfo> servers = plugin.getProxy().getServers().values();
        out.writeInt(servers.size());
        for (ServerInfo server : servers) {
            out.writeUTF(server.getName());
            out.writeInt(server.getPlayers().size());
        }

        for (ServerInfo server : plugin.getProxy().getServers().values()) {
            server.sendData("BungeeCord", out.toByteArray());
        }
    }

}
