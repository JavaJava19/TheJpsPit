package com.github.elic0de.thejpspit.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NoCollisionUtil {


    private static final Method ENTITY_GET_HANDLE_METHOD;
    private static final Method PLAYER_CONNECTION_SEND_PACKET_METHOD;
    private static final Constructor SCOREBOARD_CONSTRUCTOR;
    private static final Constructor SCOREBOARD_TEAM_CONSTRUCTOR;
    private static final Constructor PACKET_PLAY_OUT_SCOREBOARD_TEAM_B_CONSTRUCTOR;
    private static final Constructor PACKET_PLAY_OUT_SCOREBOARD_TEAM;

    private static final Field ENTITY_PLAYER_PLAYER_CONNECTION_FIELD;
    private static final Field PACKET_PLAY_OUT_SCOREBOARD_TEAM_B_FIELD;


    static {
        try {
            ENTITY_GET_HANDLE_METHOD = getCBClass("entity.CraftEntity").getMethod("getHandle");
            PLAYER_CONNECTION_SEND_PACKET_METHOD = getMojangClass("server.network.PlayerConnection").getMethod("a", getMojangClass("network.protocol.Packet"));
            SCOREBOARD_CONSTRUCTOR = getConstructor(getMojangClass("world.scores.Scoreboard"), 0);
            SCOREBOARD_TEAM_CONSTRUCTOR = getConstructor(getMojangClass("world.scores.ScoreboardTeam"), 2);
            PACKET_PLAY_OUT_SCOREBOARD_TEAM_B_CONSTRUCTOR = getConstructor(getMojangClass("network.protocol.game.PacketPlayOutScoreboardTeam$b"), 1);
            PACKET_PLAY_OUT_SCOREBOARD_TEAM = getConstructor(getMojangClass("network.protocol.game.PacketPlayOutScoreboardTeam"), 4);
            ENTITY_PLAYER_PLAYER_CONNECTION_FIELD = getMojangClass("server.level.EntityPlayer").getField("b");
            PACKET_PLAY_OUT_SCOREBOARD_TEAM_B_FIELD =  PACKET_PLAY_OUT_SCOREBOARD_TEAM_B_CONSTRUCTOR.getDeclaringClass().getDeclaredField("e");
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to create version specific server accessor", e);
        }
    }

    private static Class<?> getMojangClass(final String className) throws ClassNotFoundException {
        return Class.forName("net.minecraft." + className);
    }

    private static Class<?> getCBClass(final String className) throws ClassNotFoundException {
        return Class.forName("org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getName().split("\\.")[3]
            + "." + className);
    }

    private static Constructor<?> getConstructor(Class<?> clazz, int numParams) {
        return Arrays.stream(clazz.getDeclaredConstructors()).filter(constructor -> (constructor.getParameterCount() == numParams)).findFirst().orElse(null);
    }

    public static void writeDeclaredField(Field field, Object object, Object value) {
        if (field == null)
            return;
        field.setAccessible(true);
        try {
            field.set(object, value);
        } catch (Exception ignored) {
        }
    }

    public void sendNoCollisionPacket(Player player) {
        try {
            final Object scoreboard = SCOREBOARD_CONSTRUCTOR.newInstance();
            final Object scoreboardTeam = SCOREBOARD_TEAM_CONSTRUCTOR.newInstance(scoreboard, UUID.randomUUID().toString().substring(0, 15));
            final Object playOutScoreboardTeamB = PACKET_PLAY_OUT_SCOREBOARD_TEAM_B_CONSTRUCTOR.newInstance(scoreboardTeam);

            writeDeclaredField(PACKET_PLAY_OUT_SCOREBOARD_TEAM_B_FIELD,
                playOutScoreboardTeamB, "never");
            PACKET_PLAY_OUT_SCOREBOARD_TEAM.setAccessible(true);
            sendPacket(PACKET_PLAY_OUT_SCOREBOARD_TEAM.newInstance("", 0, Optional.of(playOutScoreboardTeamB), Stream.of(player.getName()).collect(Collectors.toList())), player);
        } catch (final ReflectiveOperationException e) {
            throw new NMSAccessException("Failed to create entity metadata packet", e);
        }
    }

    private void sendPacket(Object packet, Player player) {
        try {
            final Object handle = ENTITY_GET_HANDLE_METHOD.invoke(player);
            final Object playerConnection = ENTITY_PLAYER_PLAYER_CONNECTION_FIELD.get(handle);
            PLAYER_CONNECTION_SEND_PACKET_METHOD.invoke(playerConnection, packet);
        } catch (final ReflectiveOperationException e) {
            throw new NMSAccessException(String.format("Failed to send packet to player %s", player.getUniqueId()), e);
        }
    }

    /**
     * The nms access exception is a runtime exception that should be thrown if access to the server internals, through
     * either reflection or remapping,
     * fails due to an unexpected ABI.
     */
    public static class NMSAccessException extends RuntimeException {

        /**
         * Constructs a new runtime exception with the specified cause and a
         * detail message of {@code (cause==null ? null : cause.toString())}
         * (which typically contains the class and detail message of
         * {@code cause}).  This constructor is useful for runtime exceptions
         * that are little more than wrappers for other throwables.
         *
         * @param cause the cause (which is saved for later retrieval by the
         *              {@link #getCause()} method).  (A {@code null} value is
         *              permitted, and indicates that the cause is nonexistent or
         *              unknown.)
         *
         * @since 1.4
         */
        public NMSAccessException(Throwable cause) {
            super(cause);
        }

        /**
         * Constructs a new runtime exception with the specified detail message and
         * cause.  <p>Note that the detail message associated with
         * {@code cause} is <i>not</i> automatically incorporated in
         * this runtime exception's detail message.
         *
         * @param message the detail message (which is saved for later retrieval
         *                by the {@link #getMessage()} method).
         * @param cause   the cause (which is saved for later retrieval by the
         *                {@link #getCause()} method).  (A {@code null} value is
         *                permitted, and indicates that the cause is nonexistent or
         *                unknown.)
         *
         * @since 1.4
         */
        public NMSAccessException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}