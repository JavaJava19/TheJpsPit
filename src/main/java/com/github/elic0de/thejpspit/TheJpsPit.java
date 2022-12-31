package com.github.elic0de.thejpspit;

import co.aikar.commands.PaperCommandManager;
import com.github.elic0de.thejpspit.command.PitCommand;
import com.github.elic0de.thejpspit.database.Database;
import com.github.elic0de.thejpspit.database.SqLiteDatabase;
import com.github.elic0de.thejpspit.game.Game;
import com.github.elic0de.thejpspit.listener.EventListener;
import com.github.elic0de.thejpspit.player.PitPlayer;
import com.github.elic0de.thejpspit.player.PitPlayerManager;
import com.github.elic0de.thejpspit.util.KillRatingHelper;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public final class TheJpsPit extends JavaPlugin {

    private static TheJpsPit instance;
    private Game game;

    private Database database;

    private KillRatingHelper ratingHelper;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        // Initialize TheJpsPit
        saveConfig();
        final AtomicBoolean initialized = new AtomicBoolean(true);
        game = new Game();

        this.database = new SqLiteDatabase(this);

        initialized.set(this.database.initialize());
        if (initialized.get()) {
            getLogger().log(Level.INFO, "Successfully established a connection to the database");
        } else {
            throw new RuntimeException("Failed to establish a connection to the database. " +
                    "Please check the supplied database credentials in the config file");
        }

        ratingHelper = new KillRatingHelper(0.5);

        registerCommands();
        registerListener();

        Bukkit.getWorlds().forEach(world -> world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true));

        Bukkit.getOnlinePlayers().forEach(player ->
                database.ensureUser(PitPlayer.adapt(player)).thenRun(() ->
                        database.getPitPlayer(player).join().ifPresent(pitPlayer -> {
                            if (!PitPlayerManager.isContain(player)) PitPlayerManager.registerUser(pitPlayer);
                            pitPlayer.showHealth(pitPlayer);
                            game.join(pitPlayer);
                        })
                )
        );
    }

    private void registerCommands() {
        PaperCommandManager commandManager = new PaperCommandManager(this);

        commandManager.enableUnstableAPI("brigadier");

        commandManager.registerCommand(new PitCommand());
    }

    private void registerListener() {
        new EventListener();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (database != null) {
            database.terminate();
        }
        game.getTask().stop();

        Bukkit.getOnlinePlayers().forEach(player -> {
            final PitPlayer pitPlayer = PitPlayerManager.getPitPlayer(player);
            game.leave(pitPlayer);
            pitPlayer.getBoard().delete();
        });
    }

    public static TheJpsPit getInstance() {
        return instance;
    }

    public Game getGame() {
        return game;
    }

    public Database getDatabase() {
        return database;
    }

    public KillRatingHelper getRatingHelper() {
        return ratingHelper;
    }
}
