package com.github.elic0de.thejpspit.hook.economy;

import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.player.PitPlayer;
import gg.jps.jpscore.JpsCore;
import gg.jps.jpscore.api.i.IJpsBankApi;
import gg.jps.jpscore.db.def.strChar.PlayerUuidStr;
import gg.jps.jpscore.db.def.varchar.PassBookNote;
import gg.jps.jpscore.db.def.varchar.PluginName;
import gg.jps.jpscore.define.JpsConst;
import gg.jps.jpscore.economy.bank.def.AccountId;
import gg.jps.jpscore.economy.def.MoneyJp;

import java.math.BigDecimal;

public class JpsCoreHook extends EconomyHook {

    private IJpsBankApi api;

    public JpsCoreHook(TheJpsPit plugin) {
        super(plugin, JpsConst.pluginName.get());
    }

    @Override
    protected void onEnable() {
        if (plugin.getServer().getPluginManager().getPlugin(JpsConst.pluginName.get()) == null) {
            throw new IllegalStateException("Required plugin JpsCore could NOT FOUND");
        }
        api = JpsCore.API().JpsBankApi();
    }

    public BigDecimal getBalance(PitPlayer player){
        AccountId playerAccount = api.getAccountId(PlayerUuidStr.instance(player.getPlayer())).get();
        return api.currentCharge(playerAccount).get().value();
    }

    public boolean hasMoney(PitPlayer player, BigDecimal amount){
        AccountId playerAccount = api.getAccountId(PlayerUuidStr.instance(player.getPlayer())).get();
        return api.currentCharge(playerAccount).get().value().compareTo(amount) >= 0;
    }

    public void takeMoney(PitPlayer player, BigDecimal amount){
        api.tollBySystem(
                PlayerUuidStr.instance(player.getPlayer()), PluginName.instance(TheJpsPit.getInstance().getName()),
                new MoneyJp(amount), PassBookNote.instance("PITで徴収")
        );
        player.getBoard().updateCoins();
    }

    public void giveMoney(PitPlayer player, BigDecimal amount){
        api.giveBySystem(
                PlayerUuidStr.instance(player.getPlayer()), PluginName.instance(TheJpsPit.getInstance().getName()),
                new MoneyJp(amount), PassBookNote.instance("PITで付与"), false
        );
        player.getBoard().updateCoins();
    }

    public String formatMoney(BigDecimal amount){
        return new MoneyJp(amount).toUnitString();
    }

}
