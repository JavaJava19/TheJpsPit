package com.github.elic0de.thejpspit;

import co.aikar.commands.PaperCommandManager;
import com.github.elic0de.thejpspit.command.PitCommand;
import com.github.elic0de.thejpspit.database.Database;
import com.github.elic0de.thejpspit.database.SqLiteDatabase;
import com.github.elic0de.thejpspit.game.Game;
import com.github.elic0de.thejpspit.listener.CombatTagger;
import com.github.elic0de.thejpspit.listener.EventListener;
import com.github.elic0de.thejpspit.network.PluginMessageReceiver;
import com.github.elic0de.thejpspit.player.PitPlayer;
import com.github.elic0de.thejpspit.player.PitPlayerManager;
import com.github.elic0de.thejpspit.queue.QueueManager;
import com.github.elic0de.thejpspit.task.QueueTask;
import com.github.elic0de.thejpspit.util.killAssistHelper.KillAssistHelper;
import com.github.elic0de.thejpspit.util.KillRatingHelper;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.plugin.java.JavaPlugin;

public final class TheJpsPit extends JavaPlugin {

    private static TheJpsPit instance;
    private Game game;
    private Database database;
    private KillRatingHelper ratingHelper;
    private QueueManager queueManager;
    private QueueTask queueTask;

    public static TheJpsPit getInstance() {
        return instance;
    }

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

        ratingHelper = new KillRatingHelper(0);
        queueManager = new QueueManager();

        //queueTask = new QueueTask();

        getServer().getMessenger()
            .registerIncomingPluginChannel(this, PluginMessageReceiver.BUNGEE_CHANNEL_ID,
                new PluginMessageReceiver());
        getServer().getMessenger()
            .registerOutgoingPluginChannel(this, PluginMessageReceiver.BUNGEE_CHANNEL_ID);

        registerCommands();
        registerListener();

        Bukkit.getWorlds().forEach(world -> {
            world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
            world.setGameRule(GameRule.KEEP_INVENTORY, true);
        });

        Bukkit.getOnlinePlayers().forEach(player -> {
            final Optional<PitPlayer> userData = database.getPitPlayer(player);
            if (userData.isEmpty()) {
                database.createPitPlayer(player);
                PitPlayerManager.registerUser(new PitPlayer(player));
                return;
            }
            // Update the user's name if it has changed
            final PitPlayer pitPlayer = userData.get();
            boolean updateNeeded = false;

            if (!pitPlayer.getName().equals(player.getName())) {
                updateNeeded = true;
            }

            PitPlayerManager.registerUser(pitPlayer);
            if (updateNeeded) {
                database.updateUserData(pitPlayer);
            }
        });
    }

    private void registerCommands() {
        PaperCommandManager commandManager = new PaperCommandManager(this);

        commandManager.enableUnstableAPI("brigadier");

        commandManager.registerCommand(new PitCommand());
    }

    private void registerListener() {
        new EventListener();
        new CombatTagger();
        new KillAssistHelper();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (database != null) {
            database.terminate();
        }
        game.getTask().stop();

        //queueTask.stop();

        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        getServer().getMessenger().unregisterIncomingPluginChannel(this);

        Bukkit.getOnlinePlayers().forEach(player -> {
            final PitPlayer pitPlayer = PitPlayerManager.getPitPlayer(player);
            game.leave(pitPlayer);
            if (pitPlayer.getBoard() != null) {
                pitPlayer.getBoard().delete();
            }
        });
        Bukkit.getScheduler().cancelTasks(this);
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

    public QueueManager getQueueManager() {
        return queueManager;
    }
}
