package com.github.elic0de.thejpspit.spigot.config;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.william278.annotaml.YamlFile;
import net.william278.annotaml.YamlKey;

@YamlFile
public class Queues {

    private List<String> menuLayout = List.of(
        "         ",
        "  x y z  ",
        "         ",
        "    c    "
        );

    @YamlKey("servers")
    private Map<String, List<String>> servers = new LinkedHashMap<>(Map.of(
        "x", List.of(
            "server",
            "name",
            "description",
            "stone",
            "execute_command"))
    );

    public List<String> getMenuLayout() {
        return menuLayout;
    }

    public Map<String, List<String>> getServers() {
        return servers;
    }
}
