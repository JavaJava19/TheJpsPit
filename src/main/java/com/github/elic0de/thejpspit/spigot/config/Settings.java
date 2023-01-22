package com.github.elic0de.thejpspit.spigot.config;

import java.util.Arrays;
import java.util.List;
import net.william278.annotaml.YamlComment;
import net.william278.annotaml.YamlFile;
import net.william278.annotaml.YamlKey;

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

    public String getGithubToken() {
        return githubToken;
    }

    public List<String> getScoreboard() {
        return scoreboard;
    }
}
