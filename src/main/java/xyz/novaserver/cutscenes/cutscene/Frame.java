package xyz.novaserver.cutscenes.cutscene;

import org.bukkit.util.Vector;

public class Frame {
    private Vector position;
    private final float yaw;
    private final float pitch;

    public Frame(Vector position, float yaw, float pitch) {
        this.position = position;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Vector getPosition() {
        return position;
    }

    public void setPosition(Vector position) {
        this.position = position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }
}
