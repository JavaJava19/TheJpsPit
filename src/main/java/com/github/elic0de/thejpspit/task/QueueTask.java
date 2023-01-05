package com.github.elic0de.thejpspit.task;

import com.github.elic0de.thejpspit.TheJpsPit;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class QueueTask {

    private final TheJpsPit pit = TheJpsPit.getInstance();

    private final BukkitTask bukkitTask;

    public QueueTask() {

        final AtomicInteger repeats = new AtomicInteger();
        bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                repeats.getAndIncrement();
                if (repeats.get() >= 2) {
                    pit.getQueueManager().checkQueue();
                    repeats.set(0);
                }
            }
        }.runTaskTimer(pit, 0, 20);
    }

    public void stop() {
        bukkitTask.cancel();
    }
}
