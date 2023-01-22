package com.github.elic0de.thejpspit.spigot.config;

import net.william278.annotaml.YamlFile;
import net.william278.annotaml.YamlKey;

@YamlFile()
public class Settings {

    @YamlKey("github_token")
    private String githubToken = "";

    public String getGithubToken() {
        return githubToken;
    }
}
