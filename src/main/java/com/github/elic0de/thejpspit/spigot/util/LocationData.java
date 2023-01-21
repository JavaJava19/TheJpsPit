package com.github.elic0de.thejpspit.spigot.util;

import com.google.gson.annotations.Expose;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class LocationData {

    @Expose
    private double x;
    @Expose
    private double y;
    @Expose
    private double z;
    @Expose
    private UUID uniqueId;
    @Expose
    private float yaw;
    @Expose
    private float pitch;

    protected LocationData(double x, double y, double z, UUID uniqueId, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.uniqueId = uniqueId;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    private LocationData() {
    }
    @NotNull
    public static LocationData at(Location location) {
        return new LocationData(location.getX(), location.getY(), location.getZ(),
            location.getWorld().getUID(), location.getYaw(), location.getPitch());
    }

    @NotNull
    public static LocationData at(double x, double y, double z,  @NotNull World world, float yaw, float pitch) {
        return new LocationData(x, y, z, world.getUID(), yaw, pitch);
    }

    @NotNull
    public static LocationData at(double x, double y, double z,  @NotNull World world) {
        return new LocationData(x, y, z, world.getUID(), 0, 0);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    @NotNull
    public UUID getUniqueId() {
        return uniqueId;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public Location getLocation() {
        final World world = Bukkit.getWorld(uniqueId);
        if (world != null) return new Location(world, x, y, z, yaw, pitch);
        return Bukkit.getWorlds().stream().findAny().get().getSpawnLocation();
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setWorld(World world) {
        this.uniqueId = world.getUID();
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }


    public double distanceBetween(@NotNull LocationData other) {
        return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2) + Math.pow(z - other.z, 2));
    }

    @NotNull
    public LocationData interpolate(LocationData next, double scalar) {
        return LocationData.at(
            x + (next.x - x) * scalar,
            y + (next.y - y) * scalar,
            z + (next.z - z) * scalar,
            Bukkit.getWorld(uniqueId),
            (float) (yaw + (next.yaw - yaw) * scalar),
            (float) (pitch + (next.pitch - pitch) * scalar)
        );
    }

    @NotNull
    public String toString() {
        return "(x: " + x + ", y: " + y + ", z: " + z + ", world: " + uniqueId +
            ", yaw: " + yaw + ", pitch: " + pitch + ")";
    }
}
