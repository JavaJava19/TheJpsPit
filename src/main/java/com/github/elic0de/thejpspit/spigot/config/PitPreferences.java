package com.github.elic0de.thejpspit.spigot.config;

import com.github.elic0de.thejpspit.spigot.util.LocationData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class PitPreferences {

    @Expose
    @SerializedName("spawn")
    private LocationData spawn;

    @Expose
    @SerializedName("amount_regen_health")
    private int amountRegenHealth;

    public static PitPreferences getDefaults() {
        final Location location = Bukkit.getWorlds().stream().findAny().get().getSpawnLocation();
        return new PitPreferences(LocationData.at(location.getX(), location.getY(), location.getZ(), location.getWorld()));
    }

    private PitPreferences(LocationData spawn) {
        this.spawn = spawn;
        this.amountRegenHealth = 1;
    }

    public Optional<LocationData> getSpawn() {
        return Optional.ofNullable(spawn);
    }

    public void setSpawn(LocationData location) {
        this.spawn = location;
    }

    public int getAmountRegenHealth() {
        return amountRegenHealth;
    }

    public void setAmountRegenHealth(int amountRegenHealth) {
        this.amountRegenHealth = amountRegenHealth;
    }
}
