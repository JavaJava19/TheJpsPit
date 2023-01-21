package com.github.elic0de.thejpspit.spigot.cosmetics;

import com.github.elic0de.thejpspit.spigot.player.PitPlayer;
import java.math.BigDecimal;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public interface Cosmetic {

    String getIdentifier();

    String getDisplayName();

    String getPermission();

    List<String> getDescription();

    ItemStack getIcon();

    boolean isEnabled();

    void perkEnable(PitPlayer player);

    void prePerkEnable(PitPlayer player);

    void perkDisable(PitPlayer player);

    void prePerkDisable(PitPlayer player);

    BigDecimal getPrice();
}
