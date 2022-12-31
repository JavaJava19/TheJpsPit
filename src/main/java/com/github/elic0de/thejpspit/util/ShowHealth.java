package com.github.elic0de.thejpspit.util;

import com.github.elic0de.thejpspit.player.PitPlayer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class ShowHealth {

    public static void showHealth(PitPlayer pitPlayer, PitPlayer targetPit) {
        Player target = targetPit.getPlayer();
        double maxHealth = target.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        double health = target.getHealth() - target.getLastDamage();

        if (health < 0.0 || target.isDead()) health = 0.0;

        StringBuilder style = new StringBuilder();
        int left = 10;
        double heart = maxHealth / left;
        double halfHeart = heart / 2;
        double tempHealth = health;

        if (maxHealth != health && health >= 0 && !target.isDead()) {
            for (int i = 0; i < maxHealth; i++) {
                if (tempHealth - heart > 0) {
                    tempHealth = tempHealth - heart;

                    style.append("&4\u2764");
                    left--;
                } else {
                    break;
                }
            }

            if (tempHealth > halfHeart) {
                style.append("&4\u2764");
                left--;
            } else if (tempHealth > 0 && tempHealth <= halfHeart) {
                style.append("&c\u2764");
                left--;
            }
        }

        if (maxHealth != health) {
            style.append("&7\u2764".repeat(Math.max(0, left)));
        } else {
            style.append("&4\u2764".repeat(left));
        }

        pitPlayer.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR,
                new ComponentBuilder(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes(
                        '&', pitPlayer.getPlayer().getDisplayName() + style)
                ).create()
        );
    }

}
