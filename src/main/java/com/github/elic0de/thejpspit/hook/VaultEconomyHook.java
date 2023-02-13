package com.github.elic0de.thejpspit.hook;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.player.PitPlayer;
import java.math.BigDecimal;
import java.util.logging.Level;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultEconomyHook extends EconomyHook {

    protected Economy economy;

    public VaultEconomyHook(TheJpsPit plugin) {
        super(plugin, "Vault");
    }

    @Override
    public void onEnable() throws IllegalStateException {
        final RegisteredServiceProvider<Economy> economyProvider = plugin.getServer()
            .getServicesManager().getRegistration(Economy.class);
        if (economyProvider == null) {
            throw new IllegalStateException("Could not resolve Vault economy provider");
        }
        this.economy = economyProvider.getProvider();
        plugin.getLogger().log(Level.INFO, "Enabled Vault economy hook");
    }

    @Override
    public BigDecimal getBalance(PitPlayer player) {
        return BigDecimal.valueOf(economy.getBalance(player.getPlayer()));
    }

    @Override
    public boolean hasMoney(PitPlayer player, BigDecimal amount) {
        return getBalance(player).compareTo(amount) >= 0;
    }

    @Override
    public void takeMoney(PitPlayer player, BigDecimal amount) {
        economy.withdrawPlayer(player.getPlayer(), amount.doubleValue());
        player.getBoard().updateCoins();
    }

    @Override
    public void giveMoney(PitPlayer player, BigDecimal amount) {
        economy.depositPlayer(player.getPlayer(), amount.doubleValue());
        player.getBoard().updateCoins();
    }

    @Override
    public String formatMoney(BigDecimal amount) {
        return economy.format(amount.doubleValue());
    }

}
