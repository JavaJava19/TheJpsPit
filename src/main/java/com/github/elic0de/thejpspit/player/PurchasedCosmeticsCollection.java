package com.github.elic0de.thejpspit.player;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.cosmetics.AbstractCosmetic;
import com.github.elic0de.thejpspit.cosmetics.Cosmetic;
import com.github.elic0de.thejpspit.hook.EconomyHook;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;
import java.util.HashSet;
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
    private String selectedCosmeticId = "";

    public void selectCosmetic(AbstractCosmetic cosmetic) {
        this.selectedCosmeticId = cosmetic.getId();
    }

    public void unSelectCosmetic() {
        this.selectedCosmeticId = "";
    }

    public boolean isSelectedCosmetic(AbstractCosmetic cosmetic) {
        return this.selectedCosmeticId.equals(cosmetic.getId());
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

}
