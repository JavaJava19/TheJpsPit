package com.github.elic0de.thejpspit.listener;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.database.Database;
import com.github.elic0de.thejpspit.gui.ServerQueueMenu;
import com.github.elic0de.thejpspit.player.OfflinePitPlayer;
import com.github.elic0de.thejpspit.player.PitPlayer;
import com.github.elic0de.thejpspit.player.PitPlayerManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public class EventListener implements Listener {

    private final TheJpsPit plugin = TheJpsPit.getInstance();

    public EventListener() {
        Bukkit.getPluginManager().registerEvents(this, TheJpsPit.getInstance());
    }

    public static final Map<UUID, Entity> combatPlayers = new HashMap<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage("");

        final Player player = event.getPlayer();
        final Optional<PitPlayer> userData = plugin.getDatabase().getPitPlayer(player);
        if (userData.isEmpty()) {
            plugin.getDatabase().createPitPlayer(player);
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
            plugin.getDatabase().updateUserData(pitPlayer);
        }

        if (combatPlayers.containsKey(player.getUniqueId())) {
            LivingEntity entity = (LivingEntity) combatPlayers.get(player.getUniqueId());
            player.setHealth(entity.getHealth());
            entity.remove();
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        final PitPlayer player = PitPlayerManager.getPitPlayer(event.getPlayer());
        final UUID uuid = player.getUniqueId();
        // 直近攻撃や攻撃を受けていたら仮プレイヤーゾンビを召喚させる
        if (CombatTagger.isTagged(player.getUniqueId())) {
            LivingEntity entity = (LivingEntity) player.getPlayer().getWorld().spawnEntity(player.getPlayer().getLocation(), EntityType.ZOMBIE);
            entity.setMetadata("uuid", new FixedMetadataValue(TheJpsPit.getInstance(), uuid));
            entity.getWorld().strikeLightningEffect(entity.getLocation());
            entity.getEquipment().setArmorContents(player.getPlayer().getInventory().getArmorContents());
            entity.setCustomName(player.getName());
            entity.setHealth(player.getPlayer().getHealth());
            combatPlayers.put(uuid, entity);
        }

        PitPlayerManager.unregisterUser(player);
        plugin.getGame().leave(player);
        if (player.getBoard() != null) {
            player.getBoard().delete();
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        final LivingEntity entity = event.getEntity();
        final Database database = TheJpsPit.getInstance().getDatabase();

        if (entity.getType() != EntityType.ZOMBIE) return;
        if (entity.hasMetadata("uuid")) {
            final List<MetadataValue> metadataValues = entity.getMetadata("uuid");
            for (MetadataValue metadata : metadataValues) {
                final UUID uuid = (UUID) metadata.value();
                combatPlayers.get(uuid).remove();

                if (entity.getKiller() != null) {
                    final Player killer = entity.getKiller();
                    final PitPlayer player = PitPlayerManager.getPitPlayer(killer);
                    final Optional<OfflinePitPlayer> vitimOptional = database.getOfflinePitPlayer(uuid);

                    player.increaseKills();
                    player.increaseXP();
                    player.increaseStreaks();
                    player.addReward();

                    TheJpsPit.getInstance().getRatingHelper().initRating(player);

                    vitimOptional.ifPresent(vitim -> {
                        vitim.increaseDeaths();
                        vitim.resetStreaks();
                        TheJpsPit.getInstance().getRatingHelper().initRating(vitim);
                        database.updateUserData(vitim);

                        player.sendMessage("&b【PIT】%player%を倒しました(KDレート:%rating%)"
                            .replaceAll("%player%", entity.getCustomName())
                            .replaceAll("%rating%", vitim.getRating() + "%")
                        );
                    });
                }
            }
        }
        event.getDrops().clear();
        event.setDroppedExp(0);
    }


    @EventHandler
    public void onHungerChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
        event.setFoodLevel(20);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        plugin.getGame().death(PitPlayerManager.getPitPlayer(event.getEntity()));
        event.getDrops().clear();
        event.setDeathMessage("");
    }

    @EventHandler
    public void onDurability(PlayerItemDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player vitim) {
            if (event.getDamager() instanceof Player damager) {
                final PitPlayer victimPitPlayer = PitPlayerManager.getPitPlayer(vitim);
                final PitPlayer pitPlayer = PitPlayerManager.getPitPlayer(damager);
                if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    event.setCancelled(true);
                    return;
                }
                pitPlayer.showHealth(victimPitPlayer);
            }
        }
    }

    @EventHandler
    private void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onXpGain(PlayerExpChangeEvent event) {
        event.setAmount(0);
    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Action action = event.getAction();
        if (!(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        Player player = event.getPlayer();
        if (player.isSneaking()) {
            return;
        }
        if (event.getItem() == null) {
            return;
        }
        if (event.getItem().getType() != Material.NETHER_STAR) {
            return;
        }

        ServerQueueMenu.create(plugin, "サーバーキュー").show(PitPlayerManager.getPitPlayer(player));

        event.setCancelled(true);
    }
}
