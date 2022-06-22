package xyz.novaserver.cutscenes.cutscene.camera.compat;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;
import xyz.novaserver.cutscenes.Cutscenes;
import xyz.novaserver.cutscenes.cutscene.Frame;
import xyz.novaserver.cutscenes.cutscene.Transition;

public class FloodgateCompat implements CompatRunner {
    private final FloodgateApi floodgate = FloodgateApi.getInstance();
    private final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

    private boolean wasFlying;
    private PacketAdapter packetListener;

    @Override
    public void onSetup(Player player) {
        wasFlying = player.isFlying();
    }

    @Override
    public void onDestroy(Player player) {
        player.setFlying(wasFlying);
        if (packetListener != null) protocolManager.removePacketListener(packetListener);
        packetListener = null;
    }

    @Override
    public void onFrame(Frame frame, Player player) {}

    @Override
    public void onTransition(Transition transition, Player player) {
        if (floodgate == null || !floodgate.isFloodgatePlayer(player.getUniqueId())) return;

        if (transition.getType() == Transition.Type.FADE) {
            if (packetListener != null) return;
            // Listen for outgoing title packets and cancel them
            packetListener = new PacketAdapter(Cutscenes.getInstance(), ListenerPriority.LOWEST,
                    PacketType.Play.Server.SET_TITLE_TEXT, PacketType.Play.Server.SET_SUBTITLE_TEXT,
                    PacketType.Play.Server.SET_TITLES_ANIMATION) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    if (event.getPlayer().getUniqueId() == player.getUniqueId()) {
                        event.setCancelled(true);
                    }
                }
            };
            protocolManager.addPacketListener(packetListener);
        } else {
            if (packetListener != null) protocolManager.removePacketListener(packetListener);
            packetListener = null;
        }
    }
}
