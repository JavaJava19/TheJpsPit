package com.github.elic0de.thejpspit.spigot.task;

import com.github.elic0de.thejpspit.spigot.TheJpsPit;
import com.github.elic0de.thejpspit.spigot.nms.PacketManager;
import com.github.elic0de.thejpspit.spigot.player.PitPlayer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class GameTask {

    private final TheJpsPit pit = TheJpsPit.getInstance();

    private final BukkitTask bukkitTask;

    public GameTask() {

        final AtomicInteger repeats = new AtomicInteger();
        bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                pit.getGame().getScoreboard().update();
                repeats.getAndIncrement();
                if (repeats.get() >= 5) {
                    pit.getGame().getPitPlayers().forEach(PitPlayer::increaseHealth);
                    repeats.set(0);
                }
                // TODO クラス分ける
                for (PitPlayer pitPlayer : pit.getGame().getPitPlayers()) {
                    final long streaks = pitPlayer.getStreaks();
                    if (streaks < 5) continue;
                    final Player player = pitPlayer.getPlayer();
                    final ThreadLocalRandom random = ThreadLocalRandom.current();

                    Location spawnLocation;
                    int attempts = 0;
                    do {
                        attempts++;
                        spawnLocation = player.getLocation().add(random.nextDouble(0, 2) - 1d, 1, random.nextDouble(0, 2) - 1d);
                        if (attempts > 20) {
                            spawnLocation = player.getLocation();
                            break;
                        }
                    } while (!spawnLocation.getBlock().isPassable());

                    // figure out who should see the indicator
                    List<Player> packetRecipients = new ArrayList<>();
                    packetRecipients.add(player);
                    for (Entity nearbyEntity : player.getNearbyEntities(16, 16, 16)) {
                            if (nearbyEntity instanceof Player) packetRecipients.add((Player) nearbyEntity);
                    }

                    // Queue packet sending.
                    pit.getServer().getScheduler().runTaskAsynchronously(pit,
                        () -> summonAndQueueBountyDeletion(
                            TheJpsPit.getInstance().getPacketManager(),
                            player.getLocation().add(random.nextDouble(0, 2) - 1d, random.nextDouble(0, 2), random.nextDouble(0, 2) - 1d),
                            100 * streaks,
                            packetRecipients
                        )
                    );
                }
            }
        }.runTaskTimer(pit, 0, 5);
    }

    private void summonAndQueueBountyDeletion(PacketManager packetManager, Location location,
        long cost, List<Player> packetRecipients) {

        //Create the entity
        final Object indicatorEntity = packetManager.buildEntityArmorStand(location, ChatColor.GOLD + "" +cost);

        //Create the packets
        final Object entitySpawnPacket = packetManager.buildEntitySpawnPacket(indicatorEntity);
        final Object entityMetadataPacket = packetManager.buildEntityMetadataPacket(indicatorEntity, true);

        //Send the packets
        for (Player recipient : packetRecipients) {
            packetManager.sendPacket(entitySpawnPacket, recipient);
            packetManager.sendPacket(entityMetadataPacket, recipient);
        }

        final double startY = location.getY();
        final double speed = 0.15;
        final int[] tick = {0};
        final double[] dy = new double[1];
        final double duration = 10;

        pit.getServer().getScheduler().runTaskTimerAsynchronously(pit, runnable -> {
            //Create the destroy packet

            final Object entityDestroyPacket = packetManager.buildEntityDestroyPacket(indicatorEntity);
            dy[0] = 1;

            location.setY(startY + dy[0]);
            final Object teleportPacket = packetManager.buildTeleportPacket(indicatorEntity, location);
            for (final Player recipient : packetRecipients) {
                packetManager.sendPacket(teleportPacket, recipient);
            }
            dy[0] += speed;

            tick[0]++;
            if (tick[0] > duration) {
                //Send the destroy packet
                for (final Player recipient : packetRecipients) {
                    //packetManager.sendPacket(teleportPacket, recipient);
                    packetManager.sendPacket(entityDestroyPacket, recipient);
                }
                runnable.cancel();
            }

        }, 0, 1);
    }


    public void stop() {
        bukkitTask.cancel();
    }
}
