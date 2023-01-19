package com.github.elic0de.thejpspit.bungee;

import net.md_5.bungee.api.plugin.Plugin;

public class TheJpsPit extends Plugin {

    private static TheJpsPit instance;

    @Override
    public void onEnable() {
        instance = this;

        getProxy().registerChannel("BungeeCord");
        getProxy().getPluginManager().registerListener(this, new RequestReceiveListener());
    }

    @Override
    public void onDisable() {
        getProxy().unregisterChannel("BungeeCord");
    }

    public static TheJpsPit getInstance() {
        return instance;
    }

}
