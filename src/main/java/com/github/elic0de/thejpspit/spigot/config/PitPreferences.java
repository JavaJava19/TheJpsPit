package com.github.elic0de.thejpspit.spigot.config;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class PitPreferences {

    @Expose
    @SerializedName("spawn")
    private Location spawn;

    public static PitPreferences getDefaults() {
        return new PitPreferences(Bukkit.getWorlds().stream().findAny().get().getSpawnLocation());
    }

    private PitPreferences(Location spawn) {
        this.spawn = spawn;
    }

    public Optional<Location> getSpawn() {
        return Optional.ofNullable(spawn);
    }

    public void setSpawn(Location location) {
        this.spawn = location;
    }
}
