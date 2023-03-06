package xyz.novaserver.cutscenes.paper.data;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.novaserver.cutscenes.api.Cutscenes;
import xyz.novaserver.cutscenes.api.camera.Camera;
import xyz.novaserver.cutscenes.api.data.Animation;
import xyz.novaserver.cutscenes.api.data.Cutscene;
import xyz.novaserver.cutscenes.api.data.Transition;

public class PaperCutscene extends Cutscene {

    public PaperCutscene(Animation... animations) {
        super(animations);
    }

    public PaperCutscene(Transition startTransition, Animation... animations) {
        super(startTransition, animations);
    }

    @Override
    public void play(Camera camera, Runnable callback) {
        camera.sendTransition(startTransition());
        new BukkitRunnable() {
            final Runner runner = new Runner(callback);

            @Override
            public void run() {
                if (runner.isCancelled()) {
                    cancel();
                }
                runner.tick(camera);
            }

            @Override
            public synchronized void cancel() throws IllegalStateException {
                runner.cancel();
                super.cancel();
            }
        }.runTaskTimer((Plugin) Cutscenes.getInstance(), 0, 1);
    }
}
