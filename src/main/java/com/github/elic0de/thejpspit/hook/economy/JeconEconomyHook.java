package com.github.elic0de.thejpspit.hook.economy;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.hook.economy.EconomyHook;
import com.github.elic0de.thejpspit.player.PitPlayer;
import jp.jyn.jecon.Jecon;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.math.BigDecimal;

public class JeconEconomyHook extends EconomyHook {

    private Jecon jecon;

    public JeconEconomyHook(TheJpsPit plugin) {
        super(plugin, "Jecon");
    }

    @Override
    protected void onEnable() throws IllegalStateException {
        Plugin p = Bukkit.getPluginManager().getPlugin("Jecon");
        if (p == null || !plugin.isEnabled()) {
            throw new IllegalStateException("Could not find Jecon plugin");
        }
        this.jecon = (Jecon) p;
    }

    @Override
    public BigDecimal getBalance(PitPlayer player) {
        return jecon.getRepository().get(player.getUniqueId()).orElse(BigDecimal.ZERO);
    }

    @Override
    public boolean hasMoney(PitPlayer player, BigDecimal amount) {
        return jecon.getRepository().has(player.getUniqueId(), amount);
    }

    @Override
    public void takeMoney(PitPlayer player, BigDecimal amount) {
        jecon.getRepository().withdraw(player.getUniqueId(), amount);
        player.getBoard().updateCoins();
    }

    @Override
    public void giveMoney(PitPlayer player, BigDecimal amount) {
        jecon.getRepository().deposit(player.getUniqueId(), amount);
        player.getBoard().updateCoins();
    }

    @Override
    public String formatMoney(BigDecimal amount) {
        return jecon.getRepository().format(amount);
    }

}
