package xyz.novaserver.cutscenes.cutscene;

import org.bukkit.scheduler.BukkitRunnable;
import xyz.novaserver.cutscenes.Cutscenes;
import xyz.novaserver.cutscenes.cutscene.camera.Camera;

import java.util.ArrayList;
import java.util.List;

public class Cutscene {
    private final List<Animation> animationList = new ArrayList<>();
    private final Transition startTransition;

    public Cutscene(Animation... animations) {
        this(Transition.cut(), animations);
    }

    public Cutscene(Transition startTransition, Animation... animations) {
        this.startTransition = startTransition;
        for (Animation animation : animations) {
            this.animationList.add(animation.copy());
        }
        this.animationList.forEach(animation -> {
            animation.getFrames().forEach(f -> {
                f.setPosition(f.getPosition().add(animation.getStartPosition()));
            });
        });
    }

    public List<Animation> getAnimationList() {
        return animationList;
    }

    public void play(Camera camera) {
        play(camera, null);
    }

    public void play(Camera camera, Runnable callback) {
        camera.sendTransition(startTransition);
        new BukkitRunnable() {
            int frameIndex = 0;
            int aniIndex = 0;
            Animation animation = animationList.get(aniIndex);

            @Override
            public void run() {
                Frame frame = animation.getFrames().get(frameIndex++);
                camera.sendFrame(frame);
                if (this.frameIndex == animation.getFrames().size() - animation.getTransition().getStartFrame()) {
                    camera.sendTransition(animation.getTransition());
                }
                if (this.frameIndex == animation.getFrames().size()) {
                    if (this.aniIndex < animationList.size() - 1) {
                        frameIndex = 0;
                        animation = animationList.get(++aniIndex);
                    } else {
                        cancel();
                    }
                }
            }

            @Override
            public synchronized void cancel() throws IllegalStateException {
                if (callback != null) callback.run();
                super.cancel();
            }
        }.runTaskTimer(Cutscenes.getInstance(), 1, 1);
    }
}
