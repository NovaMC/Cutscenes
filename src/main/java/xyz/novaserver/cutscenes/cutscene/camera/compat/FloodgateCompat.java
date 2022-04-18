package xyz.novaserver.cutscenes.cutscene.camera.compat;

import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;
import xyz.novaserver.cutscenes.cutscene.Frame;
import xyz.novaserver.cutscenes.cutscene.Transition;

public class FloodgateCompat implements CompatRunner {
    private final FloodgateApi floodgate = FloodgateApi.getInstance();

    @Override
    public void onSetup(Player player) {}

    @Override
    public void onDestroy(Player player) {}

    @Override
    public void onFrame(Frame frame, Player player) {}

    @Override
    public void onTransition(Transition transition, Player player) {
        if (floodgate != null && floodgate.isFloodgatePlayer(player.getUniqueId())) {
            transition.setType(Transition.Type.CUT);
        }
    }
}
