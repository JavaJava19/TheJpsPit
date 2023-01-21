package com.github.elic0de.thejpspit.spigot.cosmetics;

import com.github.elic0de.thejpspit.spigot.player.PitPlayer;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractCosmetic implements Cosmetic {

    private static final Logger LOGGER = Bukkit.getLogger();
    private final ItemStack icon;
    private final String identifier;
    private final String displayName;
    private final String permission;
    private final List<String> description;
    private boolean enabled;

    public AbstractCosmetic(String identifier, String displayName, String permission, List<String> description) {
        this.identifier = identifier;
        this.displayName = displayName;
        this.permission = permission;
        this.description = description;
        this.icon = this.getDefaultIcon();
        this.enabled = true;
        this.validatePerkIntegrity();
    }

    public abstract ItemStack getDefaultIcon();

    private void validatePerkIntegrity() {
        if(this.getIdentifier().contains(",")){
            throw new IllegalStateException("The identifier of a perk isn't allowed to contain a comma in order to make easy database serializing possible.");
        }
        /*try {
            //NullSafety.validateNotNull(this.getDisplayName(), this.getPermission(), this.getDescription(), this.getIcon());
        } catch (NullPointerException exception) {
            this.enabled = false;
            LOGGER.log(Level.SEVERE, String.format("Cannot create instance of perk %s: %s",
                this.getClass().getName(), exception.getMessage()));
        }*/
    }

    @Override
    public void perkEnable(PitPlayer player) {
    }

    @Override
    public void perkDisable(PitPlayer player) {
    }

    @Override
    public void prePerkDisable(PitPlayer player) {
        this.perkDisable(player);
    }

    @Override
    public void prePerkEnable(PitPlayer player) {
        this.perkEnable(player);
    }

    /* the getter and setter of this class */

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getPermission() {
        return permission;
    }

    @Override
    public List<String> getDescription() {
        return description;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public ItemStack getIcon() {
        return this.icon == null ? this.getDefaultIcon() : this.icon;
    }

    @Override
    public BigDecimal getPrice() {
        return BigDecimal.ONE;
    }
}
