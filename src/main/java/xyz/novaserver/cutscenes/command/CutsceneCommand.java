package xyz.novaserver.cutscenes.command;

import com.google.common.collect.ImmutableList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.novaserver.cutscenes.Cutscenes;
import xyz.novaserver.cutscenes.cutscene.*;
import xyz.novaserver.cutscenes.cutscene.camera.Camera;
import xyz.novaserver.cutscenes.cutscene.camera.PacketCamera;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CutsceneCommand implements TabExecutor {
    private static final Logger LOGGER = Cutscenes.getInstance().getLogger();
    private final Map<UUID, AnimationRecorder> recorderMap = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player) || args.length == 0) return true;

        String arg1 = args.length > 1 ? args[1] : null;
        switch (args[0]) {
            case "play" -> play(player, arg1);
            case "record" -> record(player, arg1);
            case "stop" -> stop(player);
            case "sequence" -> sequence(player, args);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        Stream<String> possibilities = Stream.of("play", "record", "stop", "sequence");
        if (args.length == 0) {
            return possibilities.collect(Collectors.toList());
        } else if (args.length == 1) {
            return possibilities
                    .filter(name -> name.regionMatches(true, 0, args[0], 0, args[0].length()))
                    .collect(Collectors.toList());
        } else {
            return ImmutableList.of();
        }
    }

    private void logError(String msg, Exception ex, Player player) {
        player.sendMessage(Component.text(msg).color(NamedTextColor.RED));
        LOGGER.log(Level.SEVERE, msg, ex);
    }

    private void sequence(Player player, String[] args) {
        Location originalLocation = player.getLocation();

        List<Animation> animationList = new ArrayList<>();
        try {
            for (int i = 1; i < args.length; i++) {
                animationList.add(Animation.parse(new File(Cutscenes.getInstance().getAnimationFolder(), args[i] + ".yml")));
            }
        } catch (IOException e) {
            logError("An IO error was encountered while trying to load animations.", e, player);
        }

        if (!animationList.isEmpty()) {
            Camera camera = new PacketCamera(player);
            camera.setup();
            new Cutscene(animationList.toArray(new Animation[0])).play(camera, () -> {
                camera.sendFrame(new Frame(originalLocation.toVector(), originalLocation.getYaw(), originalLocation.getPitch()));
                camera.destroy();
            });
        }
    }

    private void play(Player player, String name) {
        Location originalLocation = player.getLocation();

        Animation animation = null;
        try {
            animation = Animation.parse(new File(Cutscenes.getInstance().getAnimationFolder(), name + ".yml"));
        } catch (IOException e) {
            logError("An IO error was encountered while trying to load " + name + ".yml, does it exist?", e, player);
        }

        if (animation != null) {
            Camera camera = new PacketCamera(player);
            camera.setup();
            new Cutscene(animation).play(camera, () -> {
                camera.sendFrame(new Frame(originalLocation.toVector(), originalLocation.getYaw(), originalLocation.getPitch()));
                camera.destroy();
            });
        }
    }

    private void record(Player player, String name) {
        UUID uuid = player.getUniqueId();
        if (recorderMap.containsKey(uuid)) {
            recorderMap.get(uuid).stop();
        }
        AnimationRecorder recorder = new AnimationRecorder(player);
        recorder.record(name).thenAccept(animation -> {
            player.sendMessage(Component.text("Finished recording!").color(NamedTextColor.YELLOW));
            try {
                animation.saveToFile(new File(Cutscenes.getInstance().getAnimationFolder(), animation.getName() + ".yml"));
                player.sendMessage(Component.text("Saved file as " + animation.getName() + ".yml").color(NamedTextColor.GREEN));
            } catch (IOException e) {
                logError("Failed to save animation to file!", e, player);
            }
            recorderMap.remove(uuid);
        });
        recorderMap.put(uuid, recorder);
    }

    private void stop(Player player) {
        UUID uuid = player.getUniqueId();
        if (recorderMap.containsKey(uuid)) {
            recorderMap.get(uuid).stop();
            recorderMap.remove(uuid);
        }
    }
}
