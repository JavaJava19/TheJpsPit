package com.github.elic0de.thejpspit.hook;

import com.github.elic0de.thejpspit.TheJpsPit;

public abstract class Hook {

    protected final TheJpsPit plugin;
    private final String name;
    private boolean enabled = false;

    protected Hook(TheJpsPit plugin, String name) {
        this.plugin = plugin;
        this.name = name;
    }

    protected abstract void onEnable();

    public final void enable() {
        this.onEnable();
        this.enabled = true;
    }

    public boolean isNotEnabled() {
        return !enabled;
    }

    public String getName() {
        return name;
    }

}
