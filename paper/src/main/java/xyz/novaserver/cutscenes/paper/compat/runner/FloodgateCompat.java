package xyz.novaserver.cutscenes.paper.compat.runner;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.geysermc.floodgate.api.FloodgateApi;
import xyz.novaserver.cutscenes.api.Cutscenes;
import xyz.novaserver.cutscenes.api.data.Transition;

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
    public void onTransition(Player player, Transition transition) {
        if (floodgate == null || !floodgate.isFloodgatePlayer(player.getUniqueId())) return;

        if (transition.type() == Transition.Type.FADE) {
            if (packetListener != null) return;
            // Listen for outgoing title packets and cancel them
            packetListener = new PacketAdapter((Plugin) Cutscenes.getInstance(), ListenerPriority.LOWEST,
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
