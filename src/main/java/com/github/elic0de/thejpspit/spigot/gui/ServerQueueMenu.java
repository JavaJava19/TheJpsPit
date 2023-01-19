package com.github.elic0de.thejpspit.spigot.gui;

import com.github.elic0de.thejpspit.spigot.TheJpsPit;
import com.github.elic0de.thejpspit.spigot.player.PitPlayer;
import com.github.elic0de.thejpspit.spigot.player.PitPlayerManager;
import com.github.elic0de.thejpspit.spigot.queue.QueueManager;
import com.github.elic0de.thejpspit.spigot.queue.QueueServerType;
import de.themoep.inventorygui.DynamicGuiElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ServerQueueMenu {

    private static final String[] MENU_LAYOUT = {
        "         ",
        "  x y z  ",
        "         ",
        "    c    ",
    };

    private final InventoryGui menu;

    private ServerQueueMenu(TheJpsPit plugin, String title) {
        this.menu = new InventoryGui(plugin, title, MENU_LAYOUT);

        // Add filler items
        this.menu.setFiller(new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1));
        // Add pagination handling
        this.menu.addElement(
            queueButton('x', "wool", new ItemStack(Material.PINK_WOOL),
                "CatchTheWool", "&7相手陣地にある羊毛を自分陣地へ持ち帰れ！"));
        this.menu.addElement(
            queueButton('y', "battle", new ItemStack(Material.COBBLESTONE),
                "BattleCastle", "&7自分のコアを守りつつ相手のコアを破壊せよ！！"));
        this.menu.addElement(statusButton());
        this.menu.addElement(closeButton());
    }

    public static ServerQueueMenu create(TheJpsPit plugin, String title) {
        return new ServerQueueMenu(plugin, title);
    }

    public void show(PitPlayer player) {
        menu.show(player.getPlayer());
    }

    private DynamicGuiElement queueButton(char symbol, String serverName, ItemStack itemStack,
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
                        queueManager.addQueue(pitPlayer, serverName);
                    }
                    click.getGui().draw();
                    return true;
                },
                title,
                desc,
                " ",
                "&7必要なプレイヤー: &a" + queueManager.getNeededPlayer(serverName),
                isQueued ? "&cクリックしてキャンセルする" : "&eクリックしてキューを追加",
                "&c現在利用できません"
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
