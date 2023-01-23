package com.github.elic0de.thejpspit.gui;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.hook.EconomyHook;
import com.github.elic0de.thejpspit.item.ItemManager;
import com.github.elic0de.thejpspit.item.PitItemEntry;
import com.github.elic0de.thejpspit.player.PitPlayer;
import com.github.elic0de.thejpspit.player.PitPlayerManager;
import de.themoep.inventorygui.DynamicGuiElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;

import java.math.BigDecimal;

public class ShopMenu {

    private static final String[] MENU_LAYOUT = {
            "         ",
            " SCBofLc ",
            " tvueG   ",
            "         "
    };

    private final InventoryGui menu;

    private ShopMenu(TheJpsPit plugin, String title) {
        this.menu = new InventoryGui(plugin, title, MENU_LAYOUT);
        ItemManager.getAllEntry().stream().map(this::getItemElement).forEach(this.menu::addElement);
    }

    public static ShopMenu create(TheJpsPit plugin, String title) {
        return new ShopMenu(plugin, title);
    }

    private DynamicGuiElement getItemElement(PitItemEntry pitItemEntry) {
        return new DynamicGuiElement(pitItemEntry.getSlotChar(), (viewer) -> {
            final PitPlayer pitPlayer = PitPlayerManager.getPitPlayer((Player) viewer);
            return new StaticGuiElement(pitItemEntry.getSlotChar(), pitItemEntry.getItemStack(), click -> {
                if (click.getType() == ClickType.DOUBLE_CLICK) return true;
                if (TheJpsPit.getInstance().getEconomyHook().isEmpty()) {
                    TheJpsPit.getInstance().getLogger().warning("経済プラグインが見つかりませんでした");
                    return true;
                }

                final Player player = pitPlayer.getPlayer();
                final Inventory inventory = player.getInventory();
                final EconomyHook economyHook = TheJpsPit.getInstance().getEconomyHook().get();

                if (!economyHook.hasMoney(pitPlayer, BigDecimal.valueOf(pitItemEntry.getPrice()))) {
                    pitPlayer.sendMessage("&c【PIT】所持金が足りません！");
                    return true;
                }

                if (inventory.firstEmpty() == -1) {
                    pitPlayer.sendMessage("&c【PIT】インベントリが満杯で購入できません！");
                    return true;
                }

                if (pitPlayer.getLevel() < pitItemEntry.getRequiredLevel()) {
                    pitPlayer.sendMessage("&c【PIT】レベルが足りません！");
                    return true;
                }

                economyHook.takeMoney(pitPlayer, BigDecimal.valueOf(pitItemEntry.getPrice()));
                inventory.addItem(pitItemEntry.getItemStack());
                player.updateInventory();

                return true;
            }, pitItemEntry.getLore());
        });
    }

    public void show(PitPlayer player) {
        menu.show(player.getPlayer());
    }
}
