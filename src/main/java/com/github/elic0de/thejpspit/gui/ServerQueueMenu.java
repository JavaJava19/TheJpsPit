package com.github.elic0de.thejpspit.gui;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.player.PitPlayer;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ServerQueueMenu {

    private static final String[] MENU_LAYOUT = {
            "         ",
            "  x y z  ",
            "         ",
            "    c    ",
    };

    private final InventoryGui menu;

    protected static ServerQueueMenu create(TheJpsPit plugin, String title) {
        return new ServerQueueMenu(plugin, title);
    }

    protected void show(PitPlayer player) {
        menu.show(player.getPlayer());
    }

    private ServerQueueMenu(TheJpsPit plugin, String title) {
        this.menu = new InventoryGui(plugin, title, MENU_LAYOUT);

        // Add filler items
        this.menu.setFiller(new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1));
        // Add pagination handling
        this.menu.addElement(getButton('x', new ItemStack(Material.PINK_WOOL),"CatchTheWool", "相手陣地にある羊毛を自分陣地へ持ち帰れ！"));
        this.menu.addElement(getButton('y', new ItemStack(Material.COBBLESTONE), "BattleCastle", "自分のコアを守りつつ相手のコアを破壊せよ！！"));
        this.menu.addElement(getButton('z', new ItemStack(Material.IRON_SWORD), "K/D_Rate", "kill数、Death数、レートが確認できます。"));
        this.menu.addElement(closeButton());
    }

    private StaticGuiElement getButton(char symbol, ItemStack itemStack, String title, String desc) {
        return new StaticGuiElement(symbol, itemStack,
                click -> {

                    return true;
                },
                title,
                desc
                );
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
