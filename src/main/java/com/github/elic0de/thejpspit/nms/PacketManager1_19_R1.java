package com.github.elic0de.thejpspit.nms;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.ScoreboardTeam;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Implementation of the packet manager for the 1.19 minecraft java version.
 * The implementation uses a mixture of direct calls against the re-obfuscated server internals and reflection.
 */
public final class PacketManager1_19_R1 implements PacketManager {

    private final Class<?> scoreboardClass;
    private final Method entityGetIdMethod;
    private final Method entityGetDataWatcherMethod;
    private final Method entityGetHandleMethod;
    private final Method entityGetBukkitEntityMethod;
    private final Method worldGetHandleMethod;
    private final Method playerConnectionSendPacketMethod;

    private final Field entityPlayerPlayerConnectionField;

    public PacketManager1_19_R1(final @NotNull Class scoreboardClass,
        final @NotNull Method entityGetIdMethod,
        final @NotNull Method entityGetDataWatcherMethod,
        final @NotNull Method entityGetHandleMethod,
        final @NotNull Method entityGetBukkitEntityMethod,
        final @NotNull Method worldGetHandleMethod,
        final @NotNull Method playerConnectionSendPacketMethod,
        final @NotNull Field entityPlayerPlayerConnectionField) {
        this.scoreboardClass = scoreboardClass;
        this.entityGetIdMethod = entityGetIdMethod;
        this.entityGetDataWatcherMethod = entityGetDataWatcherMethod;
        this.entityGetHandleMethod = entityGetHandleMethod;
        this.entityGetBukkitEntityMethod = entityGetBukkitEntityMethod;
        this.worldGetHandleMethod = worldGetHandleMethod;
        this.playerConnectionSendPacketMethod = playerConnectionSendPacketMethod;
        this.entityPlayerPlayerConnectionField = entityPlayerPlayerConnectionField;
    }

    @NotNull
    public static PacketManager1_19_R1 make() {
        try {
            return new PacketManager1_19_R1(
                getMojangClass("network.protocol.game.PacketPlayOutScoreboardTeam"),
                getMojangClass("world.entity.Entity").getMethod("ae"),
                getMojangClass("world.entity.Entity").getMethod("ai"),
                getCBClass("entity.CraftEntity").getMethod("getHandle"),
                getMojangClass("world.entity.Entity").getMethod("getBukkitEntity"),
                getCBClass("CraftWorld").getMethod("getHandle"),
                getMojangClass("server.network.PlayerConnection")
                    .getMethod("a", getMojangClass("network.protocol.Packet")),
                getMojangClass("server.level.EntityPlayer").getField("b")
            );
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to create version specific server accessor", e);
        }
    }

    @NotNull
    private static Class<?> getMojangClass(@NotNull final String className) throws ClassNotFoundException {
        return Class.forName("net.minecraft." + className);
    }

    @NotNull
    private static Class<?> getCBClass(@NotNull final String className) throws ClassNotFoundException {
        return Class.forName("org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getName().split("\\.")[3]
            + "." + className);
    }

    @NotNull
    @Override
    public Object buildEntitySpawnPacket(@NotNull Object entity) {
        return new PacketPlayOutSpawnEntity((EntityLiving) entity);
    }

    @NotNull
    @Override
    public Object buildScoreboardTeam(Player player) {
        try {
            final Scoreboard scoreboard = new Scoreboard();
            final ScoreboardTeam scoreboardTeam = new ScoreboardTeam(scoreboard, UUID.randomUUID().toString().substring(0, 15));
            final PacketPlayOutScoreboardTeam.b packetPlayOutScoreTeamB  = new PacketPlayOutScoreboardTeam.b(scoreboardTeam);
            final Constructor<?> sc = getConstructor(scoreboardClass, 4);
            final Field collisionRule = packetPlayOutScoreTeamB.getClass().getDeclaredField("e");

            sc.setAccessible(true);
            collisionRule.setAccessible(true);
            collisionRule.set(packetPlayOutScoreTeamB, "never");
            return sc.newInstance("", 0, Optional.of(packetPlayOutScoreTeamB), Arrays.asList(player.getName()));
        } catch (final ReflectiveOperationException e) {
            throw new NMSAccessException("Failed to create entity metadata packet", e);
        }
    }

    private Constructor<?> getConstructor(Class<?> clazz, int numParams) {
        return Arrays.stream((Constructor[])clazz.getDeclaredConstructors()).filter(constructor -> (constructor.getParameterCount() == numParams)).findFirst().orElse(null);
    }

    @NotNull
    @Override
    public Object buildEntityMetadataPacket(@NotNull Object entity, boolean forceUpdateAll) {
        try {
            final int entityId = (int) this.entityGetIdMethod.invoke(entity);
            final Object dataWatcher = this.entityGetDataWatcherMethod.invoke(entity);
            return new PacketPlayOutEntityMetadata(entityId, (DataWatcher) dataWatcher, forceUpdateAll);
        } catch (final ReflectiveOperationException e) {
            throw new NMSAccessException("Failed to create entity metadata packet", e);
        }
    }

    @NotNull
    @Override
    public Object buildEntityDestroyPacket(@NotNull Object entity) {
        try {
            final int entityId = (int) this.entityGetIdMethod.invoke(entity);
            return new PacketPlayOutEntityDestroy(entityId);
        } catch (final ReflectiveOperationException e) {
            throw new NMSAccessException("Failed to create entity destroy packet", e);
        }
    }

    @NotNull
    @Override
    public Object buildEntityArmorStand(@NotNull Location location, @NotNull String name) {
        try {
            final World world = location.getWorld();
            final WorldServer worldServer = (WorldServer) this.worldGetHandleMethod.invoke(world);

            final Object entityArmorStand = new EntityArmorStand(
                worldServer,
                location.getX(), location.getY(), location.getZ()
            );
            final ArmorStand armorStand = (ArmorStand) this.entityGetBukkitEntityMethod.invoke(entityArmorStand);
            armorStand.setMarker(true);
            armorStand.setInvisible(true);
            armorStand.setCustomNameVisible(true);
            armorStand.setCustomName(name);

            return entityArmorStand;
        } catch (final ReflectiveOperationException e) {
            throw new NMSAccessException("Failed to create new entity armor stand", e);
        }
    }

    @Override
    public void sendPacket(@NotNull Object packet, @NotNull Player player) {
        try {
            final Object handle = this.entityGetHandleMethod.invoke(player);
            final Object playerConnection = this.entityPlayerPlayerConnectionField.get(handle);
            this.playerConnectionSendPacketMethod.invoke(playerConnection, packet);
        } catch (final ReflectiveOperationException e) {
            throw new NMSAccessException(String.format("Failed to send packet to player %s", player.getUniqueId()), e);
        }
    }
}