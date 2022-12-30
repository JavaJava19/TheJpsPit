package com.github.elic0de.thejpspit.listener;

import com.github.elic0de.thejpspit.TheJpsPit;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class EventListener implements Listener {

    public EventListener() {
        Bukkit.getPluginManager().registerEvents(this, TheJpsPit.getInstance());
    }

}
