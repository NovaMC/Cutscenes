package xyz.novaserver.cutscenes.cutscene.camera.compat;

import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import org.bukkit.entity.Player;
import xyz.novaserver.cutscenes.cutscene.Frame;
import xyz.novaserver.cutscenes.cutscene.Transition;

public class TABCompat implements CompatRunner {
    private final TabAPI tabAPI = TabAPI.getInstance();
    private boolean scoreboardVisible = false;

    @Override
    public void onSetup(Player player) {
        TabPlayer tabPlayer = tabAPI.getPlayer(player.getUniqueId());
        scoreboardVisible = tabAPI.getScoreboardManager().hasScoreboardVisible(tabPlayer);
        tabAPI.getScoreboardManager().setScoreboardVisible(tabPlayer, false, false);
    }

    @Override
    public void onDestroy(Player player) {
        tabAPI.getScoreboardManager().setScoreboardVisible(
                tabAPI.getPlayer(player.getUniqueId()), scoreboardVisible, false);
    }

    @Override
    public void onFrame(Frame frame, Player player) {}

    @Override
    public void onTransition(Transition transition, Player player) {}
}
