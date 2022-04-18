package xyz.novaserver.cutscenes.cutscene;

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


    public Type getType() {
        return type;
    }

    public void setStartFrame(long startFrame) {
        this.startFrame = startFrame;
    }

    public long getStartFrame() {
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
            setStartFrame(fadeIn);
        }

        public long getFadeIn() {
            return fadeIn;
        }

        public long getStay() {
            return stay;
        }

        public long getFadeOut() {
            return fadeOut;
        }
    }
}
