package com.github.elic0de.thejpspit.spigot.config;

import java.util.Arrays;
import java.util.List;
import net.william278.annotaml.YamlComment;
import net.william278.annotaml.YamlFile;
import net.william278.annotaml.YamlKey;
import org.bukkit.ChatColor;

@YamlFile()
public class Settings {

    @YamlKey("github_token")
    @YamlComment("アクセストークンが漏洩すると、被害が広範囲に及ぶことになりますので公開しないでください")
    private String githubToken = "";


    @YamlKey("scoreboard")
    private List<String> scoreboard = Arrays.asList(
        "",
        "レベル: [%level%]",
        "JP: [%coins%]",
        "",
        "K/Dレート: &c%rating%",
        "最高レート: &b%bestRating%",
        "",
        "次のレベルまで：&a%neededXp%",
        "",
        "連続キル数: &a%streaks%",
        "最高連続キル数: &a%bestStreaks%",
        "",
        "&ejapanpvpserver.net"
    );

    @YamlKey("level")
    private List<String> level = Arrays.asList(
        "1,15,15," + ChatColor.GRAY.name(),
        "10,30,165," + ChatColor.BLUE.name(),
        "20,50,480," + ChatColor.DARK_AQUA.name(),
        "30,75,1000," + ChatColor.DARK_GREEN.name(),
        "40,125,1775," + ChatColor.GREEN.name(),
        "50,250,4550," + ChatColor.YELLOW.name(),
        "60,600,10800," + ChatColor.GOLD.name(),
        "70,800,16000," + ChatColor.RED.name(),
        "80,900,28000," + ChatColor.DARK_RED.name(),
        "90,1000,381000," + ChatColor.AQUA.name()
    );

    public String getGithubToken() {
        return githubToken;
    }

    public List<String> getScoreboard() {
        return scoreboard;
    }

    public List<String> getLevel() {
        return level;
    }
}
