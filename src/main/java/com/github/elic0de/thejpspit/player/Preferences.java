package com.github.elic0de.thejpspit.player;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Preferences {

    @Expose
    @SerializedName("kill_message")
    private boolean killMessage;

    @Expose
    @SerializedName("death_message")
    private boolean deathMessage;

    @Expose
    @SerializedName("streaks_message")
    private boolean streaksMessage;

    @Expose
    @SerializedName("cosmeticsCollection")
    private final PurchasedCosmeticsCollection cosmeticsCollection;

    public Preferences() {
        this.killMessage = true;
        this.deathMessage = true;
        this.streaksMessage = true;
        this.cosmeticsCollection = new PurchasedCosmeticsCollection();
    }

    public static Preferences getDefaults() {
        return new Preferences();
    }

    public PurchasedCosmeticsCollection getCosmeticsCollection() {
        return cosmeticsCollection;
    }

    public boolean isKillMessage() {
        return killMessage;
    }

    public boolean isDeathMessage() {
        return deathMessage;
    }

    public boolean isStreaksMessage() {
        return streaksMessage;
    }

    public void setKillMessage(boolean killMessage) {
        this.killMessage = killMessage;
    }

    public void setDeathMessage(boolean deathMessage) {
        this.deathMessage = deathMessage;
    }

    public void setStreaksMessage(boolean streaksMessage) {
        this.streaksMessage = streaksMessage;
    }
}
