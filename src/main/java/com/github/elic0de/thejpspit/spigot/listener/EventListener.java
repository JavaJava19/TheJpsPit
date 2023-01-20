package com.github.elic0de.thejpspit.spigot.listener;

import com.github.elic0de.thejpspit.spigot.TheJpsPit;
import com.github.elic0de.thejpspit.spigot.gui.ServerQueueMenu;
import com.github.elic0de.thejpspit.spigot.item.IUsable;
import com.github.elic0de.thejpspit.spigot.item.ItemManager;
import com.github.elic0de.thejpspit.spigot.item.PitItemEntry;
import com.github.elic0de.thejpspit.spigot.nms.PacketManager;
import com.github.elic0de.thejpspit.spigot.player.PitPlayer;
import com.github.elic0de.thejpspit.spigot.player.PitPlayerManager;
import com.github.elic0de.thejpspit.spigot.villager.VillagerNPC;
import com.github.elic0de.thejpspit.spigot.villager.VillagerNPCManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;
import java.util.Optional;

public class EventListener implements Listener {

    private final TheJpsPit plugin = TheJpsPit.getInstance();

    public EventListener() {
        Bukkit.getPluginManager().registerEvents(this, TheJpsPit.getInstance());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage("");

        final Player player = event.getPlayer();
        final Optional<PitPlayer> userData = plugin.getDatabase().getPitPlayer(player);

        final PacketManager packetManager = TheJpsPit.getInstance().getPacketManager();
        Object packet = packetManager.buildScoreboardTeam(player);
        packetManager.sendPacket(packet, player);

        /*plugin.getPitPreferences().ifPresent(pitPreferences -> {
            final Location location = pitPreferences.getSpawn().get();

            if (location != null) player.teleport(location);
        });*/
        if (userData.isEmpty()) {
            plugin.getDatabase().createPitPlayer(player);
            PitPlayerManager.registerUser(new PitPlayer(player));
            return;
        }
        // Update the user's name if it has changed
        final PitPlayer pitPlayer = userData.get();
        boolean updateNeeded = !pitPlayer.getName().equals(player.getName());

        PitPlayerManager.registerUser(pitPlayer);
        if (updateNeeded) {
            plugin.getDatabase().updateUserData(pitPlayer);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        final PitPlayer player = PitPlayerManager.getPitPlayer(event.getPlayer());
        // 直近攻撃や攻撃を受けていて離脱したらペナルティーとして最後に与えたプレイヤーのキルとする
        if (CombatTagger.isTagged(player.getUniqueId())) {
            plugin.getGame().death(player);
        }

        PitPlayerManager.unregisterUser(player);
        plugin.getGame().leave(player);
        if (player.getBoard() != null) {
            player.getBoard().delete();
        }
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        final Player player = event.getPlayer();

        if (event.getItem().getType() == Material.GOLDEN_APPLE) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5 * 20, 2));
            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 60 * 2, 0));
        }
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
            if (event.getCause() == DamageCause.VOID) {
                plugin.getGame().death(PitPlayerManager.getPitPlayer(vitim));
                return;
            }
            if (event.getDamager() instanceof Player damager) {
                final PitPlayer victimPitPlayer = PitPlayerManager.getPitPlayer(vitim);
                final PitPlayer pitPlayer = PitPlayerManager.getPitPlayer(damager);
                switch (event.getCause()) {
                    case FALL -> {
                        event.setCancelled(true);
                        return;
                    }
                    case PROJECTILE -> event.setDamage(event.getDamage() - (event.getDamage() / 0.8));
                }

                pitPlayer.showHealth(victimPitPlayer);
                victimPitPlayer.setLastDamager(pitPlayer);
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
        /*final PitPlayer pitPlayer = PitPlayerManager.getPitPlayer(event.getPlayer());
        pitPlayer.increaseXP();*/
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
        if (event.getItem().getType() == Material.NETHER_STAR) {
            ServerQueueMenu.create(plugin, "サーバーキュー").show(PitPlayerManager.getPitPlayer(player));
        } else if (ItemManager.isPitItem(event.getItem())) {
            PitItemEntry pitItemEntry = Objects.requireNonNull(ItemManager.getPitItemEntry(event.getItem()));
            if (pitItemEntry instanceof IUsable usablePitItem) {
                usablePitItem.use(event.getPlayer());
            }
        } else {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void on(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (!(entity instanceof Villager villager)) return;
        if (VillagerNPCManager.isVillagerNPC(villager)) {
            VillagerNPC villagerNPC = Objects.requireNonNull(VillagerNPCManager.getVillagerNPC(villager));
            villagerNPC.click(event.getPlayer());
            event.setCancelled(true);
        }
    }
}
