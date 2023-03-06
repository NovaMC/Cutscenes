package xyz.novaserver.cutscenes.paper.data;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.novaserver.cutscenes.api.Cutscenes;
import xyz.novaserver.cutscenes.api.data.Animation;
import xyz.novaserver.cutscenes.api.data.Frame;
import xyz.novaserver.cutscenes.api.data.Vector3f;

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
                frames.add(new Frame(new Vector3f(location.getX(), location.getY(), location.getZ()),
                        location.getYaw(), location.getPitch()));
                if (frames.size() >= MAX_FRAMES) stop();
            }

            @Override
            public synchronized void cancel() throws IllegalStateException {
                future.complete(new Animation(name, frames));
                super.cancel();
            }
        };
        this.runnable.runTaskTimer((Plugin) Cutscenes.getInstance(), 1, 1);

        return future;
    }
}
