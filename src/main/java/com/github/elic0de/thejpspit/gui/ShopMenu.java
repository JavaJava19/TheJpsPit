package com.github.elic0de.thejpspit.gui;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.hook.EconomyHook;
import com.github.elic0de.thejpspit.item.ItemManager;
import com.github.elic0de.thejpspit.item.PitItem;
import com.github.elic0de.thejpspit.player.PitPlayer;
import com.github.elic0de.thejpspit.player.PitPlayerManager;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.math.BigDecimal;

public class ShopMenu {

    private static final String[] MENU_LAYOUT = {
            "         ",
            "  1234   ",
            "         ",
    };

    private final InventoryGui menu;

    private ShopMenu(TheJpsPit plugin, String title) {
        this.menu = new InventoryGui(plugin, title, MENU_LAYOUT);
        this.menu.addElement(getItemElement('1', ItemManager.getPitItem("diamond_sword")));
        this.menu.addElement(getItemElement('2', ItemManager.getPitItem("diamond_chestplate")));
        this.menu.addElement(getItemElement('3', ItemManager.getPitItem("diamond_boots")));
        this.menu.addElement(getItemElement('4', ItemManager.getPitItem("obsidian")));
    }

    public static ShopMenu create(TheJpsPit plugin, String title) {
        return new ShopMenu(plugin, title);
    }

    private StaticGuiElement getItemElement(char slotChar, PitItem pitItem) {
        return new StaticGuiElement(slotChar, pitItem.getShopItem(), click -> {
            if (click.getWhoClicked() instanceof Player) return true;
            if (TheJpsPit.getInstance().getEconomyHook().isEmpty()) return true;

            final Player player = (Player) click.getWhoClicked();
            final PitPlayer pitPlayer = PitPlayerManager.getPitPlayer(player);
            final Inventory inventory = player.getInventory();
            final EconomyHook economyHook = TheJpsPit.getInstance().getEconomyHook().get();

            if (!economyHook.hasMoney(pitPlayer, BigDecimal.valueOf(pitItem.getPrice()))) {
                player.sendMessage("&c【PIT】所持金が足りません！");
                return true;
            }

            if (inventory.firstEmpty() == -1) {
                player.sendMessage("&c【PIT】インベントリが満杯で購入できません！");
                return true;
            }

            economyHook.takeMoney(pitPlayer, BigDecimal.valueOf(pitItem.getPrice()));
            inventory.addItem(pitItem.getItemStack());
            player.updateInventory();

            return true;
        });
    }

    public void show(PitPlayer player) {
        menu.show(player.getPlayer());
    }
}
