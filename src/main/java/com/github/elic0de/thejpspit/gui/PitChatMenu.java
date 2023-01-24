package com.github.elic0de.thejpspit.gui;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.hook.EconomyHook;
import com.github.elic0de.thejpspit.item.PitItemEntry;
import com.github.elic0de.thejpspit.player.PitPlayer;
import com.github.elic0de.thejpspit.player.PitPlayerManager;
import de.themoep.inventorygui.DynamicGuiElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PitChatMenu {

    private static final String[] MENU_LAYOUT = {
        "         ",
        " SCBofLc ",
        "         ",
        "    x    "
    };

    private final InventoryGui menu;

    private PitChatMenu(TheJpsPit plugin, String title) {
        this.menu = new InventoryGui(plugin, title, MENU_LAYOUT);
        // Add filler items
        this.menu.setFiller(new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1));
        // Add pagination handling
        this.menu.addElement(
            toggleButton('C', PitChatType.KILL,  new ItemStack(Material.REDSTONE),
                "キルメッセージ", "&7プレイヤーをキルした際のメッセージ"));
        this.menu.addElement(
            toggleButton('B', PitChatType.DEATH,  new ItemStack(Material.SKELETON_SKULL),
                "デスメッセージ", "&7死亡した際のメッセージ"));
        this.menu.addElement(
            toggleButton('o', PitChatType.STREAKS,  new ItemStack(Material.GOLDEN_APPLE),
                "ストリークメッセージ", "&7ストリーク"));
        this.menu.addElement(closeButton());
    }

    private DynamicGuiElement toggleButton(char symbol, PitChatType type, ItemStack itemStack,
        String title, String desc) {
        return new DynamicGuiElement(symbol, (viewer) -> {
            final PitPlayer pitPlayer = PitPlayerManager.getPitPlayer((Player) viewer);
            AtomicBoolean toggle = new AtomicBoolean(true);
            pitPlayer.getPreferences().ifPresent(preferences -> {
                switch (type) {
                    case KILL -> toggle.set(preferences.isKillMessage());
                    case DEATH -> toggle.set(preferences.isDeathMessage());
                    case STREAKS -> toggle.set(preferences.isStreaksMessage());
                }
            });
            return new StaticGuiElement(symbol, itemStack,
                click -> {
                    pitPlayer.getPreferences().ifPresent(preferences -> {
                        switch (type) {
                            case KILL -> preferences.setKillMessage(!toggle.get());
                            case DEATH -> preferences.setDeathMessage(!toggle.get());
                            case STREAKS -> preferences.setStreaksMessage(!toggle.get());
                        }
                    });
                    click.getGui().draw();
                    return true;
                },
                title,
                desc,
                " ",
                toggle.get() ? "&aON" : "&cOFF"
            );
        });
    }

    private StaticGuiElement closeButton() {
        return new StaticGuiElement('x', new ItemStack(Material.BARRIER),
            click -> {
                click.getWhoClicked().closeInventory();
                return true;
            },
            "&c閉じる"
        );
    }

    public static PitChatMenu create(TheJpsPit plugin, String title) {
        return new PitChatMenu(plugin, title);
    }

    public void show(PitPlayer player) {
        menu.show(player.getPlayer());
    }

    public enum PitChatType {
        KILL,
        DEATH,
        STREAKS
    }
}
