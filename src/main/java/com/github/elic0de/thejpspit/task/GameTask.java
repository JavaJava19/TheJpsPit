package com.github.elic0de.thejpspit.task;

import com.github.elic0de.thejpspit.TheJpsPit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class GameTask {

    private final TheJpsPit pit = TheJpsPit.getInstance();

    private final BukkitTask bukkitTask;

    public GameTask() {
        bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {

            }
        }.runTaskTimer(pit, 0, 20);
    }

    public void stop() {
        bukkitTask.cancel();
    }
}
