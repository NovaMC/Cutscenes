package xyz.novaserver.cutscenes.cutscene.camera.compat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.novaserver.cutscenes.cutscene.Frame;
import xyz.novaserver.cutscenes.cutscene.Transition;
import xyz.novaserver.placeholders.paper.Main;
import xyz.novaserver.placeholders.paper.actionbar.ActionbarManager;
import xyz.novaserver.placeholders.paper.actionbar.ActionbarPlayer;

public class ActionbarCompat implements CompatRunner {
    private ActionbarManager manager;

    public ActionbarCompat() {
        Main placeholders = (Main) Bukkit.getServer().getPluginManager().getPlugin("NovaPlaceholders");
        if (placeholders != null) {
            manager = placeholders.getActionbarManager();
        }
    }

    @Override
    public void onSetup(Player player) {
        ActionbarPlayer actionbarPlayer = manager.getActionbarPlayer(player.getUniqueId());
        if (!actionbarPlayer.getCurrentActionbar().hide()) return;
        if (!actionbarPlayer.isCancelled()) {
            actionbarPlayer.cancel();
            actionbarPlayer.clear();
        }
    }

    @Override
    public void onDestroy(Player player) {
        ActionbarPlayer actionbarPlayer = manager.getActionbarPlayer(player.getUniqueId());
        if (!actionbarPlayer.getCurrentActionbar().hide()) return;
        actionbarPlayer.schedule();
    }

    @Override
    public void onFrame(Frame frame, Player player) {}

    @Override
    public void onTransition(Transition transition, Player player) {}
}
