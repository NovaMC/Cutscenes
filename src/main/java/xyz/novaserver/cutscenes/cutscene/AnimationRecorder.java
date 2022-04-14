package xyz.novaserver.cutscenes.cutscene;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.novaserver.cutscenes.Cutscenes;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class AnimationRecorder {
    private final Player player;
    private BukkitRunnable runnable;

    public AnimationRecorder(Player player) {
        this.player = player;
    }

    public void stop() {
        // If player is recording cancel and remove task
        if (runnable != null && !runnable.isCancelled()) {
            runnable.cancel();
            runnable = null;
        }
    }

    public CompletableFuture<Animation> record(String name) {
        CompletableFuture<Animation> future = new CompletableFuture<>();

        // Start new recording and store runnable
        this.runnable = new BukkitRunnable() {
            final int MAX_FRAMES = 200;
            final List<Frame> frames = new ArrayList<>();

            @Override
            public void run() {
                Location location = player.getLocation();
                frames.add(new Frame(location.toVector(), location.getYaw(), location.getPitch()));
                if (frames.size() >= MAX_FRAMES) stop();
            }

            @Override
            public synchronized void cancel() throws IllegalStateException {
                future.complete(new Animation(name, frames));
                super.cancel();
            }
        };
        this.runnable.runTaskTimer(Cutscenes.getInstance(), 1, 1);

        return future;
    }
}
