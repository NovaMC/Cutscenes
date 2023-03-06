package xyz.novaserver.cutscenes.api.data;

import xyz.novaserver.cutscenes.api.camera.Camera;

import java.util.ArrayList;
import java.util.List;

public abstract class Cutscene {
    private final List<Animation> animationList = new ArrayList<>();
    private final Transition startTransition;

    public Cutscene(Animation... animations) {
        this(Transition.cut(), animations);
    }

    public Cutscene(Transition startTransition, Animation... animations) {
        this.startTransition = startTransition;
        for (Animation animation : animations) {
            this.animationList.add(animation.clone());
        }
        this.animationList.forEach(animation -> {
            animation.frames().forEach(f -> {
                f.position(f.position().add(animation.startPosition()));
            });
        });
    }

    public List<Animation> animationList() {
        return animationList;
    }

    public Transition startTransition() {
        return startTransition;
    }

    public void play(Camera camera) {
        play(camera, null);
    }

    public abstract void play(Camera camera, Runnable callback);


    protected class Runner {
        private int frameIndex = 0;
        private int aniIndex = 0;
        private boolean isCancelled = false;

        private Animation animation;
        private final Runnable callback;

        public Runner(Runnable callback) {
            this.animation = animationList.get(aniIndex);
            this.callback = callback;
        }

        public boolean isCancelled() {
            return isCancelled;
        }

        public void cancel() {
            if (isCancelled) return;
            isCancelled = true;
            if (callback != null) callback.run();
        }

        public void tick(Camera camera) {
            if (isCancelled) return;
            Frame frame = animation.frames().get(frameIndex++);
            camera.sendFrame(frame);
            if (this.frameIndex == animation.frames().size() - animation.transition().startFrame()) {
                camera.sendTransition(animation.transition());
            }
            if (this.frameIndex == animation.frames().size()) {
                if (this.aniIndex < animationList.size() - 1) {
                    frameIndex = 0;
                    animation = animationList.get(++aniIndex);
                    camera.sceneChange(animation.frames().get(0));
                } else {
                    cancel();
                }
            }
        }
    }
}
