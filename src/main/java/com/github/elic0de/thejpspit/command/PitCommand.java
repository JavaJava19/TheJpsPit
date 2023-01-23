package com.github.elic0de.thejpspit.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.github.elic0de.thejpspit.TheJpsPit;
import com.github.elic0de.thejpspit.item.ItemManager;
import com.github.elic0de.thejpspit.item.PitItemEntry;
import com.github.elic0de.thejpspit.player.PitPlayer;
import com.github.elic0de.thejpspit.player.PitPlayerManager;
import com.github.elic0de.thejpspit.util.LocationData;
import com.github.elic0de.thejpspit.villager.VillagerNPCManager;
import org.bukkit.entity.Player;

@CommandAlias("pit|tjp|p")
public class PitCommand extends BaseCommand {

    private final TheJpsPit pit = TheJpsPit.getInstance();

    @Subcommand("data")
    @CommandPermission("tjp.data")
    public void onCommand(Player player, @Optional OnlinePlayer onlinePlayer) {
        final PitPlayer pitPlayer = PitPlayerManager.getPitPlayer(player);

        if (onlinePlayer == null) {
            pitPlayer.sendStatus();
            return;
        }

        pitPlayer.sendStatus(PitPlayerManager.getPitPlayer(onlinePlayer.getPlayer()));
    }

    @Subcommand("reset")
    @CommandPermission("tjp.reset")
    public void onReset(Player player) {
        pit.getDatabase().deletePlayerData();
    }

    @Subcommand("set spawn")
    @CommandPermission("tjp.spawn")
    public void onSetSpawn(Player player) {
        pit.getPitPreferences().ifPresent(pitPreferences -> pitPreferences.setSpawn(LocationData.at(player.getLocation())));
        pit.getPitPreferences().ifPresent(preferences -> pit.getDatabase().updatePitPreferences(preferences));
        PitPlayerManager.getPitPlayer(player).sendMessage("&cスポーン地点を設定しました");
    }

    @Subcommand("set regen")
    @CommandPermission("tjp.regen")
    public void onRegen(Player player, int amount) {
        pit.getPitPreferences().ifPresent(pitPreferences -> pitPreferences.setAmountRegenHealth(amount));
        pit.getPitPreferences().ifPresent(preferences -> pit.getDatabase().updatePitPreferences(preferences));
        player.sendMessage("変更しました");
    }

    @Subcommand("set killcoin")
    @CommandPermission("tjp.killcoin")
    public void onKillCoin(Player player, int amount) {
        pit.getPitPreferences().ifPresent(pitPreferences -> pitPreferences.setAmountReward(amount));
        pit.getPitPreferences().ifPresent(preferences -> pit.getDatabase().updatePitPreferences(preferences));
        player.sendMessage("変更しました");
    }

    @Subcommand("set arrowDamage")
    @CommandPermission("tjp.arrow")
    public void onArrowDamage(Player player, double amount) {
        pit.getPitPreferences().ifPresent(pitPreferences -> pitPreferences.setDamageAmount(amount));
        pit.getPitPreferences().ifPresent(preferences -> pit.getDatabase().updatePitPreferences(preferences));
        player.sendMessage("変更しました");
    }

    @Subcommand("shop")
    @CommandPermission("tjp.shop")
    public void onCreateShop(Player player) {
        VillagerNPCManager.getVillagerNPC("shop").spawnAt(player.getWorld(), player.getLocation());
    }

    @Subcommand("item")
    @CommandPermission("tjp.item")
    public void onGetItem(Player player, String itemId) {
        final PitItemEntry entry = ItemManager.getPitItemEntry(itemId);
        if (entry == null) return;
        player.getInventory().addItem(entry.getItemStack());
    }

    @Subcommand("reload")
    @CommandPermission("tjp.reload")
    public void onReload(Player player) {
        TheJpsPit.getInstance().reload();
        player.sendMessage("リロードしました");
    }
}
