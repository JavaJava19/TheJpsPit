package com.github.elic0de.thejpspit.spigot.config;

import net.william278.annotaml.YamlComment;
import net.william278.annotaml.YamlFile;
import net.william278.annotaml.YamlKey;

@YamlFile()
public class Settings {

    @YamlKey("github_token")
    @YamlComment("アクセストークンが漏洩すると、被害が広範囲に及ぶことになりますので公開しないでください")
    private String githubToken = "";

    public String getGithubToken() {
        return githubToken;
    }
}
