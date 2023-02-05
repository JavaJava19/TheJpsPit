package com.github.elic0de.thejpspit.hook;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.player.PitPlayer;
import java.math.BigDecimal;
import java.util.logging.Level;
import net.tnemc.core.TNE;
import net.tnemc.core.common.api.TNEAPI;
import org.jetbrains.annotations.NotNull;

public class TneEconomyHook extends EconomyHook {

    protected TNEAPI tneAPI;

    public TneEconomyHook(TheJpsPit plugin) {
        super(plugin, "TheNewEconomy");
    }

    @Override
    public void onEnable() throws IllegalStateException {
        this.tneAPI = TNE.instance().api();
        plugin.getLogger().log(Level.INFO, "Enabled Vault economy hook");
    }

    private BigDecimal getBalance(@NotNull PitPlayer player) {
        if (tneAPI.getAccount(player.getUniqueId()) == null) return BigDecimal.ZERO;
        return tneAPI.getAccount(player.getUniqueId()).getHoldings(player.getPlayer().getWorld().getName());
    }

    @Override
    public boolean hasMoney(PitPlayer player, BigDecimal amount) {
        return getBalance(player).compareTo(amount) >= 0;
    }

    @Override
    public void takeMoney(PitPlayer player, BigDecimal amount) {
        tneAPI.getAccount(player.getUniqueId()).removeHoldings(amount);
    }

    @Override
    public void giveMoney(PitPlayer player, BigDecimal amount) {
        tneAPI.getAccount(player.getUniqueId()).addHoldings(amount);
    }

    @Override
    public String formatMoney(BigDecimal amount) {
        return "tneAPI.format(amount.doubleValue(), world);";
    }

}
