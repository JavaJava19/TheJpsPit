package com.github.elic0de.thejpspit;

import co.aikar.commands.PaperCommandManager;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.github.elic0de.thejpspit.command.PitCommand;
import com.github.elic0de.thejpspit.command.SpawnCommand;
import com.github.elic0de.thejpspit.config.PitPreferences;
import com.github.elic0de.thejpspit.database.Database;
import com.github.elic0de.thejpspit.database.SqLiteDatabase;
import com.github.elic0de.thejpspit.game.Game;
import com.github.elic0de.thejpspit.hook.EconomyHook;
import com.github.elic0de.thejpspit.hook.Hook;
import com.github.elic0de.thejpspit.hook.VaultEconomyHook;
import com.github.elic0de.thejpspit.item.ItemManager;
import com.github.elic0de.thejpspit.item.items.ItemDiamondBoots;
import com.github.elic0de.thejpspit.item.items.ItemDiamondChestPlate;
import com.github.elic0de.thejpspit.item.items.ItemDiamondSword;
import com.github.elic0de.thejpspit.item.items.ItemObsidian;
import com.github.elic0de.thejpspit.listener.CombatTagger;
import com.github.elic0de.thejpspit.listener.EventListener;
import com.github.elic0de.thejpspit.network.PluginMessageReceiver;
import com.github.elic0de.thejpspit.nms.PacketManager;
import com.github.elic0de.thejpspit.nms.PacketManager1_19_R1;
import com.github.elic0de.thejpspit.player.PitPlayer;
import com.github.elic0de.thejpspit.player.PitPlayerManager;
import com.github.elic0de.thejpspit.queue.QueueManager;
import com.github.elic0de.thejpspit.task.QueueTask;
import com.github.elic0de.thejpspit.util.KillAssistHelper;
import com.github.elic0de.thejpspit.util.KillRatingHelper;
import com.github.elic0de.thejpspit.villager.VillagerNPCManager;
import com.github.elic0de.thejpspit.villager.villagers.ShopVillager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;



import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public final class TheJpsPit extends JavaPlugin {

    private static TheJpsPit instance;
    private Game game;
    private Database database;
    private KillRatingHelper ratingHelper;

    private KillAssistHelper assistKillHelper;
    private QueueManager queueManager;
    private QueueTask queueTask;
    private List<Hook> hooks = new ArrayList<>();

    private Optional<PitPreferences> pitPreferences;

    private PacketManager packetManager;

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

        assistKillHelper = new KillAssistHelper();
        ratingHelper = new KillRatingHelper(0);
        queueManager = new QueueManager();

        setPreferences();
        setPacketManager();

        //queueTask = new QueueTask();

        getServer().getMessenger()
                .registerIncomingPluginChannel(this, PluginMessageReceiver.BUNGEE_CHANNEL_ID,
                        new PluginMessageReceiver());
        getServer().getMessenger()
                .registerOutgoingPluginChannel(this, PluginMessageReceiver.BUNGEE_CHANNEL_ID);

        registerCommands();
        registerListener();
        registerHooks();

        loadHooks();

        Bukkit.getWorlds().forEach(world -> {
            //world.setGameRule(GameRule., true);
            world.setGameRule(GameRule.KEEP_INVENTORY, true);
        });

        createItems();
        createNPCs();

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


    private void setPreferences() {
        final Optional<PitPreferences> preferences = database.getPitPreferences();
        if (preferences.isEmpty()) {
            database.createPitPreferences(PitPreferences.getDefaults());
            this.pitPreferences = Optional.of(PitPreferences.getDefaults());
            return;
        }
        this.pitPreferences = preferences;
    }

    private void setPacketManager() {
        final String serverVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].trim();
        PacketManager packetManager;
        switch (serverVersion) {
            case "v1_19_R1":
                packetManager = PacketManager1_19_R1.make();
                break;
            default:
                throw new RuntimeException("Failed to create version specific server accessor");
        }
        this.packetManager = packetManager;
    }

    private void registerCommands() {
        PaperCommandManager commandManager = new PaperCommandManager(this);

        commandManager.enableUnstableAPI("brigadier");

        commandManager.registerCommand(new PitCommand());
        commandManager.registerCommand(new SpawnCommand());
    }

    private void registerListener() {
        new EventListener();
        new CombatTagger();
    }

    private void registerHooks() {
        final PluginManager plugins = Bukkit.getPluginManager();
        if (plugins.getPlugin("Vault") != null) {
            this.registerHook(new VaultEconomyHook(this));
        }
    }

    private void createItems() {
        ItemManager.register(new ItemDiamondSword());
        ItemManager.register(new ItemDiamondChestPlate());
        ItemManager.register(new ItemDiamondBoots());
        ItemManager.register(new ItemObsidian());
    }

    private void createNPCs() {
        VillagerNPCManager.register(new ShopVillager());
    }

    @Override
    public void onDisable() {
        pitPreferences.ifPresent(preferences -> database.updatePitPreferences(preferences));
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

    public KillAssistHelper getAssistKillHelper() {
        return assistKillHelper;
    }

    public QueueManager getQueueManager() {
        return queueManager;
    }

    public Optional<PitPreferences> getPitPreferences() {
        return pitPreferences;
    }

    public PacketManager getPacketManager() {
        return packetManager;
    }

    public Gson getGson() {
        return Converters.registerOffsetDateTime(new GsonBuilder().excludeFieldsWithoutExposeAnnotation()).create();
    }
}
