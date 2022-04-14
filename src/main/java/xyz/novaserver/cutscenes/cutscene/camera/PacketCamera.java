package xyz.novaserver.cutscenes.cutscene.camera;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import xyz.novaserver.cutscenes.Cutscenes;
import xyz.novaserver.cutscenes.cutscene.Frame;
import xyz.novaserver.cutscenes.cutscene.Transition;
import xyz.novaserver.cutscenes.cutscene.camera.compat.*;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PacketCamera extends Camera {
    private static final Plugin plugin = Cutscenes.getInstance();
    private static final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

    private final Set<CompatListener> compatListeners = new HashSet<>();
    private final Set<CompatRunner> compatRunners = new HashSet<>();
    private PacketAdapter packetListener;

    public PacketCamera(@NotNull Player player) {
        super(player);
    }

    private void loadCompatibility() {
        // Register compatibility with various plugins
        PluginManager pluginManager = Bukkit.getPluginManager();
        if (pluginManager.isPluginEnabled("Themis")) {
            compatListeners.add(new ThemisListener(player.getUniqueId()));
        }
        if (pluginManager.isPluginEnabled("floodgate")) {
            compatRunners.add(new FloodgateCompat());
        }
        if (pluginManager.isPluginEnabled("TAB")) {
            compatRunners.add(new TABCompat());
        }
        compatListeners.forEach(compatListener -> pluginManager.registerEvents(compatListener, plugin));
    }

    @Override
    public void setup() {
        loadCompatibility();
        // Run any needed setup for compatibility
        compatRunners.forEach(compatRunner -> compatRunner.onSetup(player));

        // Listen for incoming movement packets and cancel them
        packetListener = new PacketAdapter(plugin, ListenerPriority.LOWEST,
                PacketType.Play.Client.POSITION, PacketType.Play.Client.POSITION_LOOK) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (event.getPlayer().getUniqueId() == player.getUniqueId()) {
                    event.setCancelled(true);
                }
            }
        };
        protocolManager.addPacketListener(packetListener);

        // Set client to spectator mode
        sendPacket(getGameModePacket(GameMode.SPECTATOR));
        sendPacket(getPlayerInfoPacket(GameMode.SPECTATOR));
    }

    @Override
    public void destroy() {
        compatRunners.forEach(compatRunner -> compatRunner.onDestroy(player));
        compatListeners.forEach(CompatListener::unregister);
        if (packetListener != null) protocolManager.removePacketListener(packetListener);

        // Set client back to their actual gamemode
        sendPacket(getGameModePacket(player.getGameMode()));
        sendPacket(getPlayerInfoPacket(player.getGameMode()));
    }

    @Override
    public void sendFrame(@NotNull Frame frame) {
        compatRunners.forEach(compatRunner -> compatRunner.onFrame(frame, player));

        PacketContainer teleport = new PacketContainer(PacketType.Play.Server.POSITION);
        teleport.getDoubles()
                .write(0, frame.getPosition().getX())
                .write(1, frame.getPosition().getY())
                .write(2, frame.getPosition().getZ());
        teleport.getFloat()
                .write(0, frame.getYaw())
                .write(1, frame.getPitch());
        teleport.getBooleans().write(0, true);

        sendPacket(teleport);
    }

    @Override
    public void sendTransition(@NotNull Transition transition) {
        compatRunners.forEach(compatRunner -> compatRunner.onTransition(transition, player));

        if (transition.getType() == Transition.Type.FADE) {
            final long TICK_MILLIS = 50;
            final Transition.Fade fade = (Transition.Fade) transition;
            final Title.Times times = Title.Times.times(
                    Duration.ofMillis(fade.getFadeIn() * TICK_MILLIS),
                    Duration.ofMillis(fade.getStay() * TICK_MILLIS),
                    Duration.ofMillis(fade.getFadeOut() * TICK_MILLIS));
            final String text = plugin.getConfig().getString("transition.fade-text", "");
            player.showTitle(Title.title(Component.text(text), Component.empty(), times));
        }
    }

    /*
    // Various methods to help with usage of packets
    */

    private void sendPacket(PacketContainer packet) {
        try {
            protocolManager.sendServerPacket(player, packet);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Failed to send packet", e);
        }
    }

    private PacketContainer getGameModePacket(GameMode gameMode) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.GAME_STATE_CHANGE);
        packet.getGameStateIDs().write(0, 3);
        packet.getFloat().write(0, (float) gameMode.getValue());
        return packet;
    }

    private PacketContainer getPlayerInfoPacket(GameMode gameMode) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
        packet.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.UPDATE_GAME_MODE);
        packet.getPlayerInfoDataLists().write(0, Collections.singletonList(
                new PlayerInfoData(WrappedGameProfile.fromPlayer(player), player.getPing(),
                        EnumWrappers.NativeGameMode.fromBukkit(gameMode), WrappedChatComponent.fromText(player.getPlayerListName()))));
        return packet;
    }
}
