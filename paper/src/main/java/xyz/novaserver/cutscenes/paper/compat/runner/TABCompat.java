package xyz.novaserver.cutscenes.paper.compat.runner;

import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import org.bukkit.entity.Player;

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
}
