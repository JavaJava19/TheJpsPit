package com.github.elic0de.thejpspit.util.killAssistHelper;

import com.github.elic0de.thejpspit.TheJpsPit;
import de.themoep.minedown.MineDown;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class KillAssistHelper implements Listener {

    public KillAssistHelper() {
        Bukkit.getPluginManager().registerEvents(this, TheJpsPit.getInstance());
    }

    public static void test(Player player) {
        player.spigot().sendMessage(new MineDown("[(+2 assist)](hover=04o7)").toComponent());
    }
}
