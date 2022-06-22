package xyz.novaserver.cutscenes.cutscene.camera.compat;

import org.bukkit.entity.Player;
import xyz.novaserver.cutscenes.cutscene.Frame;
import xyz.novaserver.cutscenes.cutscene.Transition;

public interface CompatRunner {
    void onSetup(Player player);

    void onDestroy(Player player);

    void onFrame(Frame frame, Player player);

    void onTransition(Transition transition, Player player);
}
