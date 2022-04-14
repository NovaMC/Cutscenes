package xyz.novaserver.cutscenes.cutscene.camera;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.novaserver.cutscenes.cutscene.Frame;
import xyz.novaserver.cutscenes.cutscene.Transition;

public abstract class Camera {
    protected final Player player;

    protected Camera(@NotNull Player player) {
        this.player = player;
    }

    public abstract void setup();

    public abstract void destroy();

    public abstract void sendFrame(@NotNull Frame frame);

    public abstract void sendTransition(@NotNull Transition transition);
}
