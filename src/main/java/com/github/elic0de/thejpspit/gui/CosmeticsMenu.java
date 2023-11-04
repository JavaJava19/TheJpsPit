package com.github.elic0de.thejpspit.gui;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.cosmetics.AbstractCosmetic;
import com.github.elic0de.thejpspit.player.PitPlayer;
import com.github.elic0de.thejpspit.player.PitPlayerManager;
import com.github.elic0de.thejpspit.player.PurchasedCosmeticsCollection;
import de.themoep.inventorygui.DynamicGuiElement;
import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.GuiPageElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CosmeticsMenu {

    private final int pageNumber = 1;

    private static final String[] MENU_LAYOUT = {
            "         ",
            " ppppppp ",
            " ppppppp ",
            " ppppppp ",
            "         ",
            "bl  i  ne"
    };

    private final InventoryGui menu;

    private CosmeticsMenu(TheJpsPit plugin, String title) {
        this.menu = new InventoryGui(plugin, title, MENU_LAYOUT);
        this.menu.addElement(getPositionGroup(plugin.getCosmeticManager().getAllCosmetics().values().stream().collect(
            Collectors.toList())));
        menu.addElement(new GuiPageElement('b',
            new ItemStack(Material.ARROW),
            GuiPageElement.PageAction.FIRST,
            "最初のページ"));
        menu.addElement(new GuiPageElement('l',
            new ItemStack(Material.ARROW),
            GuiPageElement.PageAction.PREVIOUS,
            "前のページ"));
        menu.addElement(new GuiPageElement('n',
            new ItemStack(Material.ARROW),
            GuiPageElement.PageAction.NEXT,
            "次のページ"));
        menu.addElement(new GuiPageElement('e',
            new ItemStack(Material.ARROW),
            GuiPageElement.PageAction.LAST,
           "最後のページ"));
        menu.setPageNumber(pageNumber);
    }

    public static CosmeticsMenu create(TheJpsPit plugin, String title) {
        return new CosmeticsMenu(plugin, title);
    }

    private GuiElementGroup getPositionGroup(List<AbstractCosmetic> cosmetics) {
        final GuiElementGroup group = new GuiElementGroup('p');
        cosmetics.forEach(position -> group.addElement(getItemElement(position)));
        return group;
    }


    private DynamicGuiElement getItemElement(AbstractCosmetic cosmetic) {
        return new DynamicGuiElement(cosmetic.getSlot(), (viewer) -> {
            final PitPlayer pitPlayer = PitPlayerManager.getPitPlayer((Player) viewer);
            final AtomicBoolean hasCosmetic = new AtomicBoolean(false);
            final AtomicBoolean isSelected = new AtomicBoolean(false);
            pitPlayer.getPreferences().ifPresent(preferences -> {
                final PurchasedCosmeticsCollection cosmeticsCollection = preferences.getCosmeticsCollection();
                hasCosmetic.set(cosmeticsCollection.has(cosmetic));
                isSelected.set(cosmeticsCollection.isSelectedCosmetic(cosmetic));
            });
            return new StaticGuiElement(cosmetic.getSlot(), new ItemStack(cosmetic.getMaterial()), click -> {
                pitPlayer.getPreferences().ifPresent(preferences -> {
                    final PurchasedCosmeticsCollection cosmeticsCollection = preferences.getCosmeticsCollection();
                    if (hasCosmetic.get()) {
                        if (isSelected.get()) {
                            cosmeticsCollection.unSelectCosmetic(cosmetic);
                            return;
                        }
                        cosmeticsCollection.selectCosmetic(cosmetic);
                        return;
                    }
                    if (cosmeticsCollection.canBuy(pitPlayer, cosmetic)) {
                        cosmeticsCollection.buy(pitPlayer, cosmetic);
                        return;
                    }
                    pitPlayer.sendMessage("たりない");
                });
                click.getGui().draw();
                return false;
            }, "&a" + cosmetic.getName(),
                "&7" + cosmetic.getDescription(),
                " ",
                "&e" + cosmetic.getCoin() + "JP",
                isSelected.get() ? "&c解除する": hasCosmetic.get() ? "&a選択する" : "&c購入する"
            );
        });
    }

    public void show(PitPlayer player) {
        menu.show(player.getPlayer());
    }
}
