package com.github.elic0de.thejpspit.spigot.gui;

import com.github.elic0de.thejpspit.spigot.TheJpsPit;
import com.github.elic0de.thejpspit.spigot.player.PitPlayer;
import com.github.elic0de.thejpspit.spigot.player.PitPlayerManager;
import com.github.elic0de.thejpspit.spigot.queue.QueueManager;
import de.themoep.inventorygui.DynamicGuiElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ServerQueueMenu {

    private final InventoryGui menu;

    private ServerQueueMenu(TheJpsPit plugin, String title) {
        final String[] MENU_LAYOUT = plugin.getQueues().getMenuLayout().toArray(new String[0]);
        this.menu = new InventoryGui(plugin, title, MENU_LAYOUT);

        // Add filler items
        this.menu.setFiller(new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1));
        // Add pagination handling

        final Map<String, List<String>> queues = plugin.getQueues().getServers();

        queues.forEach((s, info) -> {
            if (info.size() != 5) return;
            final String serverName = info.get(0);
            final String name = info.get(1);
            final String description = info.get(2);
            final String icon = info.get(3).toUpperCase(Locale.ROOT);
            final String command = info.get(4);
            final Material material = Material.getMaterial(icon);
            this.menu.addElement(
                queueButton(String.valueOf(s).charAt(0), serverName, command, new ItemStack(material == null ? Material.BARRIER : material),
                    name, description));
        });
        this.menu.addElement(statusButton());
        this.menu.addElement(closeButton());
    }

    public static ServerQueueMenu create(TheJpsPit plugin, String title) {
        return new ServerQueueMenu(plugin, title);
    }

    public void show(PitPlayer player) {
        menu.show(player.getPlayer());
    }

    private DynamicGuiElement queueButton(char symbol, String serverName, String command , ItemStack itemStack,
        String title, String desc) {
        return new DynamicGuiElement(symbol, (viewer) -> {
            final QueueManager queueManager = TheJpsPit.getInstance().getQueueManager();
            final PitPlayer pitPlayer = PitPlayerManager.getPitPlayer((Player) viewer);
            final boolean isQueued = TheJpsPit.getInstance().getQueueManager().isQueued(pitPlayer, serverName);
            return new StaticGuiElement(symbol, itemStack,
                click -> {
                    if (isQueued) {
                        queueManager.cancelQueue(pitPlayer);
                    } else {
                        queueManager.addQueue(pitPlayer, serverName, command);
                    }
                    click.getGui().draw();
                    return true;
                },
                title,
                desc,
                " ",
                "&7必要なプレイヤー: &a" + queueManager.getNeededPlayer(serverName),
                isQueued ? "&cクリックしてキャンセルする" : "&eクリックしてキューを追加"
            );
        });
    }

    private DynamicGuiElement statusButton() {
        return new DynamicGuiElement('z', (viewer) -> {
            final PitPlayer pitPlayer = PitPlayerManager.getPitPlayer((Player) viewer);
            return new StaticGuiElement('z', new ItemStack(Material.IRON_SWORD),
                click -> true,
                "Pit Status",
                " ",
                "&7Kills: &c" + pitPlayer.getKills(),
                "&7Deaths: &e" + pitPlayer.getDeaths(),
                "&7Rating: &a" + pitPlayer.getRating()
            );
        });
    }

    private StaticGuiElement closeButton() {
        return new StaticGuiElement('c', new ItemStack(Material.BARRIER),
            click -> {
                click.getWhoClicked().closeInventory();
                return true;
            },
            "&c閉じる"
        );
    }

}
