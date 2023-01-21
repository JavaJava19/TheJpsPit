package com.github.elic0de.thejpspit.spigot.cosmetics.cosmetic;

import com.github.elic0de.thejpspit.spigot.cosmetics.type.AbstractListenerCosmetic;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.particle.ParticleEffect;

public class TestEffect extends AbstractListenerCosmetic {


    public TestEffect() {
        super("TestEffect", "TestEffect", "TestEffect", List.of(""));
    }

    @Override
    public ItemStack getDefaultIcon() {
        return new ItemStack(Material.COMPASS);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        ParticleEffect.FLAME.display(player.getLocation());
    }
}
