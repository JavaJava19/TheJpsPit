package com.github.elic0de.thejpspit.spigot.hook;

import com.github.elic0de.thejpspit.spigot.TheJpsPit;
import com.github.elic0de.thejpspit.spigot.player.PitPlayer;
import java.math.BigDecimal;

public abstract class EconomyHook extends Hook {

    protected EconomyHook(TheJpsPit plugin, String name) {
        super(plugin, name);
    }

    public abstract BigDecimal getBalance(PitPlayer player);

    public abstract boolean hasMoney(PitPlayer player, BigDecimal amount);

    public abstract void takeMoney(PitPlayer player, BigDecimal amount);

    public abstract void giveMoney(PitPlayer player, BigDecimal amount);

    public abstract String formatMoney(BigDecimal amount);

}
