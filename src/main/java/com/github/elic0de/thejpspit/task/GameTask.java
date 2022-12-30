package com.github.elic0de.thejpspit.task;

import com.github.elic0de.thejpspit.TheJpsPit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.atomic.AtomicInteger;

public class GameTask {

    private final TheJpsPit pit = TheJpsPit.getInstance();

    private final BukkitTask bukkitTask;

    public GameTask() {

        final AtomicInteger repeats = new AtomicInteger();
        bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                pit.getGame().getScoreboard().update();
                repeats.getAndIncrement();
                if (repeats.get() >= 5) {
                    pit.getGame().getPitPlayers().forEach(pitPlayer -> pitPlayer.increaseHealth());
                    repeats.set(0);
                }
            }
        }.runTaskTimer(pit, 0, 20);
    }

    public void stop() {
        bukkitTask.cancel();
    }
}
