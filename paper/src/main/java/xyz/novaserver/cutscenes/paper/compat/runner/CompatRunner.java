package xyz.novaserver.cutscenes.paper.compat.runner;

import org.bukkit.entity.Player;
import xyz.novaserver.cutscenes.api.data.Transition;
import xyz.novaserver.cutscenes.api.data.Frame;

public interface CompatRunner {
    default void onSetup(Player player) {}

    default void onDestroy(Player player) {}

    default void onFrame(Player player, Frame frame) {}

    default void onTransition(Player player, Transition transition) {}
}
