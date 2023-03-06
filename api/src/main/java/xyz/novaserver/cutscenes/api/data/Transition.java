package xyz.novaserver.cutscenes.api.data;

public class Transition {
    private final Type type;
    // # of frames from the end of an animation to start transition
    private long startFrame = 0;

    private Transition(Type type) {
        this.type = type;
    }


    public static Transition cut() {
        return new Transition(Type.CUT);
    }

    public static Fade fade(long fadeIn, long stay, long fadeOut) {
        return new Fade(Type.FADE, fadeIn, stay, fadeOut);
    }


    public Type type() {
        return type;
    }

    public void startFrame(long startFrame) {
        this.startFrame = startFrame;
    }

    public long startFrame() {
        return startFrame;
    }

    public enum Type {
        CUT,
        FADE
    }


    public static class Fade extends Transition {
        private final long fadeIn;
        private final long stay;
        private final long fadeOut;

        private Fade(Type type, long fadeIn, long stay, long fadeOut) {
            super(type);
            this.fadeIn = fadeIn;
            this.stay = stay;
            this.fadeOut = fadeOut;
            startFrame(fadeIn);
        }

        public long fadeIn() {
            return fadeIn;
        }

        public long stay() {
            return stay;
        }

        public long fadeOut() {
            return fadeOut;
        }
    }
}
