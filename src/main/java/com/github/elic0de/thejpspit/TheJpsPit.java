package com.github.elic0de.thejpspit;

import com.github.elic0de.thejpspit.game.Game;
import org.bukkit.plugin.java.JavaPlugin;

public final class TheJpsPit extends JavaPlugin {

    private static TheJpsPit instance;
    private Game game;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        game = new Game();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static TheJpsPit getInstance() {
        return instance;
    }

    public Game getGame() {
        return game;
    }

    private void registerCommands() {

    }

    private void registerListeners() {

    }
}
