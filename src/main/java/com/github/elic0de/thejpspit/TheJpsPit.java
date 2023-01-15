package com.github.elic0de.thejpspit;

import co.aikar.commands.PaperCommandManager;
import com.github.elic0de.thejpspit.command.PitCommand;
import com.github.elic0de.thejpspit.database.Database;
import com.github.elic0de.thejpspit.database.SqLiteDatabase;
import com.github.elic0de.thejpspit.game.Game;
import com.github.elic0de.thejpspit.hook.EconomyHook;
import com.github.elic0de.thejpspit.hook.Hook;
import com.github.elic0de.thejpspit.hook.VaultEconomyHook;
import com.github.elic0de.thejpspit.listener.CombatTagger;
import com.github.elic0de.thejpspit.listener.EventListener;
import com.github.elic0de.thejpspit.network.PluginMessageReceiver;
import com.github.elic0de.thejpspit.player.PitPlayer;
import com.github.elic0de.thejpspit.player.PitPlayerManager;
import com.github.elic0de.thejpspit.queue.QueueManager;
import com.github.elic0de.thejpspit.task.QueueTask;
import com.github.elic0de.thejpspit.util.killAssistHelper.KillAssistHelper;
import com.github.elic0de.thejpspit.util.KillRatingHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

public final class TheJpsPit extends JavaPlugin {

    private static TheJpsPit instance;
    private Game game;
    private Database database;
    private KillRatingHelper ratingHelper;
    private QueueManager queueManager;
    private QueueTask queueTask;
    private List<Hook> hooks = new ArrayList<>();

    private Scoreboard scoreboard;
    private Team team;

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

        this.hooks = new ArrayList<>();

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

        optionScoreboard();

        //queueTask = new QueueTask();

        getServer().getMessenger()
            .registerIncomingPluginChannel(this, PluginMessageReceiver.BUNGEE_CHANNEL_ID,
                new PluginMessageReceiver());
        getServer().getMessenger()
            .registerOutgoingPluginChannel(this, PluginMessageReceiver.BUNGEE_CHANNEL_ID);

        registerCommands();
        registerListener();
        registerHooks();

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
            boolean updateNeeded = !pitPlayer.getName().equals(player.getName());

            PitPlayerManager.registerUser(pitPlayer);
            if (updateNeeded) {
                database.updateUserData(pitPlayer);
            }

        });
    }

    public void addPitTeam(Player player) {
        team.addEntry(player.getName());
    }

    public void removePitTeam(Player player) {
        team.removeEntry(player.getName());
    }

    private void optionScoreboard() {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        team = scoreboard.registerNewTeam("pit");
        team.setOption(Option.COLLISION_RULE, OptionStatus.NEVER);
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

    private void registerHooks() {
        final PluginManager plugins = Bukkit.getPluginManager();
        if (plugins.getPlugin("Vault") != null) {
            this.registerHook(new VaultEconomyHook(this));
        }
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

    private List<Hook> getHooks() {
        return hooks;
    }

    private void registerHook(Hook hook) {
        getHooks().add(hook);
    }

    private void loadHooks() {
        getHooks().stream().filter(Hook::isNotEnabled).forEach(Hook::enable);
        getLogger().log(Level.INFO, "Successfully loaded " + getHooks().size() + " hooks");
    }

    private <T extends Hook> Optional<T> getHook(Class<T> hookClass) {
        return getHooks().stream()
            .filter(hook -> hookClass.isAssignableFrom(hook.getClass()))
            .map(hookClass::cast)
            .findFirst();
    }

    public Optional<EconomyHook> getEconomyHook() {
        return getHook(EconomyHook.class);
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
