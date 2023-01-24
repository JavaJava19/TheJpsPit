package com.github.elic0de.thejpspit.gui;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.cosmetics.Cosmetic;
import com.github.elic0de.thejpspit.player.PitPlayer;
import com.github.elic0de.thejpspit.player.PitPlayerManager;
import com.github.elic0de.thejpspit.player.PurchasedCosmeticsCollection;
import de.themoep.inventorygui.DynamicGuiElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.units.qual.A;

public class CosmeticsMenu {

    private static final String[] MENU_LAYOUT = {
            "         ",
            " SCBoFLc ",
            " tvueG   ",
            "         "
    };

    private final InventoryGui menu;

    private CosmeticsMenu(TheJpsPit plugin, String title) {
        this.menu = new InventoryGui(plugin, title, MENU_LAYOUT);
        plugin.getCosmeticManager().getKillCosmetics().values().stream().map(killCosmetic -> getItemElement((Cosmetic) killCosmetic)).forEach(this.menu::addElement);
    }

    public static CosmeticsMenu create(TheJpsPit plugin, String title) {
        return new CosmeticsMenu(plugin, title);
    }

    private DynamicGuiElement getItemElement(Cosmetic cosmetic) {
        return new DynamicGuiElement(cosmetic.getId().charAt(0), (viewer) -> {
            final PitPlayer pitPlayer = PitPlayerManager.getPitPlayer((Player) viewer);
            final AtomicBoolean hasCosmetic = new AtomicBoolean(false);
            final AtomicBoolean isSelected = new AtomicBoolean(false);
            pitPlayer.getPreferences().ifPresent(preferences -> {
                final PurchasedCosmeticsCollection cosmeticsCollection = preferences.getCosmeticsCollection();
                hasCosmetic.set(cosmeticsCollection.has(cosmetic));
                isSelected.set(cosmeticsCollection.isSelectedCosmetic(cosmetic));
            });
            return new StaticGuiElement(cosmetic.getName().charAt(0), new ItemStack(Material.COMPASS), click -> {
                pitPlayer.getPreferences().ifPresent(preferences -> {
                    final PurchasedCosmeticsCollection cosmeticsCollection = preferences.getCosmeticsCollection();
                    if (hasCosmetic.get()) {
                        if (isSelected.get()) {
                            cosmeticsCollection.unSelectCosmetic();
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
                return true;
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
