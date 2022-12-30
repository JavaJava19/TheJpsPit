package com.github.elic0de.thejpspit;

import org.bukkit.plugin.java.JavaPlugin;

public final class TheJpsPit extends JavaPlugin {

    private static TheJpsPit instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static TheJpsPit getInstance() {
        return instance;
    }

    private void registerCommands() {

    }

    private void registerListeners() {

    }
}
