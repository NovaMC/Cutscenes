package xyz.novaserver.cutscenes.api.data;

public class Frame {
    private Vector3f position;
    private final float yaw;
    private final float pitch;

    public Frame(Vector3f position, float yaw, float pitch) {
        this.position = position;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Vector3f position() {
        return position;
    }

    public Frame position(Vector3f position) {
        this.position = position;
        return this;
    }

    public float pitch() {
        return pitch;
    }

    public float yaw() {
        return yaw;
    }

    @Override
    public String toString() {
        return "Frame{" +
                "position=" + position +
                ", yaw=" + yaw +
                ", pitch=" + pitch +
                '}';
    }
}
