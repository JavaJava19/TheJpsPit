package com.github.elic0de.thejpspit.player;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.cosmetics.AbstractCosmetic;
import com.github.elic0de.thejpspit.cosmetics.type.AuraCosmetic;
import com.github.elic0de.thejpspit.cosmetics.type.DeathCosmetic;
import com.github.elic0de.thejpspit.cosmetics.type.KillCosmetic;
import com.github.elic0de.thejpspit.cosmetics.type.StreakCosmetic;
import com.github.elic0de.thejpspit.cosmetics.type.TrailCosmetic;
import com.github.elic0de.thejpspit.hook.economy.EconomyHook;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class PurchasedCosmeticsCollection {

    private final TheJpsPit pit = TheJpsPit.getInstance();
    private final Optional<EconomyHook> economyHook = pit.getEconomyHook();

    @Expose
    @SerializedName("cosmetics_id")
    private final Set<String> cosmeticsId = new HashSet<>();

    @Expose
    @SerializedName("selected_id")
    private Map<String, String> selectedCosmeticId = new HashMap<>();



    // todo:もっといいやりかたがあるはずなので教えてください
    public void selectCosmetic(AbstractCosmetic cosmetic) {
        final String key = getKey(cosmetic);
        if (key == null) return;
        this.selectedCosmeticId.put(key, cosmetic.getId());
    }

    public void unSelectCosmetic(AbstractCosmetic cosmetic) {
        final String key = getKey(cosmetic);
        selectedCosmeticId.put(key, "");
        //this.selectedCosmeticId.containsValue()
    }

    public boolean isSelectedCosmetic(AbstractCosmetic cosmetic) {
        final String key = getKey(cosmetic);
        if (key == null) return false;
        if (selectedCosmeticId.containsKey(key))
            return this.selectedCosmeticId.get(key) == cosmetic.getId();
        return false;
    }

    public boolean canBuy(PitPlayer pitPlayer, AbstractCosmetic cosmetic){
        final AtomicBoolean canBuy = new AtomicBoolean(false);
        economyHook.ifPresent(economyHook -> canBuy.set(economyHook.hasMoney(pitPlayer, BigDecimal.valueOf(cosmetic.getCoin()))));
        return canBuy.get();
    }

    public void buy(PitPlayer pitPlayer, AbstractCosmetic cosmetic){
        economyHook.ifPresent(economyHook -> economyHook.takeMoney(pitPlayer, BigDecimal.valueOf(cosmetic.getCoin())));
        cosmeticsId.add(cosmetic.getId());
    }

    public boolean has(AbstractCosmetic cosmetic){
        return cosmeticsId.contains(cosmetic.getId());
    }


    private String getKey(AbstractCosmetic cosmetic) {
        String key = null;
        if (cosmetic instanceof KillCosmetic) key = "kill";
        if (cosmetic instanceof DeathCosmetic) key = "death";
        if (cosmetic instanceof StreakCosmetic) key = "streak";
        if (cosmetic instanceof TrailCosmetic) key = "trail";
        if (cosmetic instanceof AuraCosmetic) key = "aura";
        return key;
    }
}
