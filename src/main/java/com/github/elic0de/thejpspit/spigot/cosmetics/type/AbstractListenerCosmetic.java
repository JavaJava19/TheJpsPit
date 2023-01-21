package com.github.elic0de.thejpspit.spigot.cosmetics.type;

import com.github.elic0de.thejpspit.spigot.TheJpsPit;
import com.github.elic0de.thejpspit.spigot.cosmetics.AbstractCosmetic;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public abstract class AbstractListenerCosmetic extends AbstractCosmetic implements Listener {

    public AbstractListenerCosmetic(String identifier, String displayName, String permission, List<String> description) {
        super(identifier, displayName, permission, description);
        Bukkit.getPluginManager().registerEvents(this, TheJpsPit.getInstance());
    }
}
