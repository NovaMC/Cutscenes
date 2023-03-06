package xyz.novaserver.cutscenes.paper.camera;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import xyz.novaserver.cutscenes.api.Cutscenes;
import xyz.novaserver.cutscenes.api.camera.Camera;
import xyz.novaserver.cutscenes.api.camera.FakeEntity;
import xyz.novaserver.cutscenes.api.data.Transition;
import xyz.novaserver.cutscenes.api.data.Frame;
import xyz.novaserver.cutscenes.paper.CutscenesPaper;
import xyz.novaserver.cutscenes.paper.compat.CompatHolder;
import xyz.novaserver.cutscenes.paper.compat.listener.CompatListener;
import xyz.novaserver.cutscenes.paper.compat.runner.CompatRunner;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PaperCamera implements Camera {
    private static final CutscenesPaper cutscenes = (CutscenesPaper) Cutscenes.getInstance();

    private final Set<CompatListener> compatListeners = new HashSet<>();
    private final Set<CompatRunner> compatRunners = new HashSet<>();

    private final Player player;
    private final FakeEntity fakeEntity;

    public PaperCamera(Player player) {
        this.player = player;
        this.fakeEntity = new FakeEntity(cutscenes.getNextEntityId(), UUID.randomUUID());
    }

    private void setupCompatibility() {
        // Register compatibility with various plugins
        PluginManager pluginManager = Bukkit.getPluginManager();
        CompatHolder.COMPAT_LISTENERS.forEach(holder -> {
            if (pluginManager.isPluginEnabled(holder.getPluginName())) {
                try {
                    compatListeners.add(holder.getClazz().getDeclaredConstructor().newInstance());
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        CompatHolder.COMPAT_RUNNERS.forEach(holder -> {
            if (pluginManager.isPluginEnabled(holder.getPluginName())) {
                try {
                    compatRunners.add(holder.getClazz().getDeclaredConstructor().newInstance());
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // Run any needed setup for compatibility
        // We assume that cutscenes is a Bukkit plugin
        compatListeners.forEach(compatListener -> pluginManager.registerEvents(compatListener, cutscenes));
        compatRunners.forEach(compatRunner -> compatRunner.onSetup(player));
    }

    @Override
    public void initialize() {
        setupCompatibility();

        UUID viewer = player.getUniqueId();
        Location loc = player.getLocation();

        // Set client to spectator mode
        cutscenes.getPacketSender().changeGamemode(viewer, GameMode.SPECTATOR.name());
        // Spawn fake armor stand and set as player's camera
        cutscenes.getPacketSender().spawnEntity(viewer,
                EntityType.ARMOR_STAND.name(),
                fakeEntity.getEntityUUID(),
                fakeEntity.getEntityId(),
                loc.getX(), loc.getY(), loc.getZ(),
                loc.getPitch(), loc.getYaw());
        cutscenes.getPacketSender().setCamera(viewer, fakeEntity.getEntityId());
    }

    @Override
    public void destroy() {
        compatRunners.forEach(compatRunner -> compatRunner.onDestroy(player));
        compatListeners.forEach(CompatListener::unregister);

        UUID viewer = player.getUniqueId();

        // Unset player's camera and destroy armor stand
        cutscenes.getPacketSender().setCamera(viewer, player.getEntityId());
        cutscenes.getPacketSender().destroyEntity(viewer, fakeEntity.getEntityId());
        // Set client back to their actual gamemode
        cutscenes.getPacketSender().changeGamemode(viewer, player.getGameMode().name());
    }

    @Override
    public void sendFrame(@NotNull Frame frame) {
        compatRunners.forEach(compatRunner -> compatRunner.onFrame(player, frame));

        UUID viewer = player.getUniqueId();

        cutscenes.getPacketSender().teleportEntity(viewer,
                fakeEntity.getEntityId(),
                frame.position().x(), frame.position().y(), frame.position().z(),
                frame.pitch(), frame.yaw());
        cutscenes.getPacketSender().rotateHead(viewer, fakeEntity.getEntityId(), frame.yaw());
    }

    @Override
    public void sendTransition(@NotNull Transition transition) {
        compatRunners.forEach(compatRunner -> compatRunner.onTransition(player, transition));

        if (transition.type() == Transition.Type.FADE) {
            final long TICK_MILLIS = 50;
            final Transition.Fade fade = (Transition.Fade) transition;
            final Title.Times times = Title.Times.times(
                    Duration.ofMillis(fade.fadeIn() * TICK_MILLIS),
                    Duration.ofMillis(fade.stay() * TICK_MILLIS),
                    Duration.ofMillis(fade.fadeOut() * TICK_MILLIS));
            final String text = cutscenes.getConfig().getString("transition.fade-text", "");
            player.showTitle(Title.title(Component.text(text), Component.empty(), times));
        }
    }

    @Override
    public void sceneChange(Frame frame) {
        compatRunners.forEach(compatRunner -> compatRunner.onFrame(player, frame));

        UUID viewer = player.getUniqueId();

        cutscenes.getPacketSender().destroyEntity(viewer, fakeEntity.getEntityId());
        cutscenes.getPacketSender().spawnEntity(viewer,
                EntityType.ARMOR_STAND.name(),
                fakeEntity.getEntityUUID(),
                fakeEntity.getEntityId(),
                frame.position().x(), frame.position().y(), frame.position().z(),
                frame.pitch(), frame.yaw());
        cutscenes.getPacketSender().setCamera(viewer, fakeEntity.getEntityId());
    }
}
