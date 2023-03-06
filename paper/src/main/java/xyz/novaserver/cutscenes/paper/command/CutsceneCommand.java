package xyz.novaserver.cutscenes.paper.command;

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
import xyz.novaserver.cutscenes.api.Cutscenes;
import xyz.novaserver.cutscenes.api.camera.Camera;
import xyz.novaserver.cutscenes.api.data.Animation;
import xyz.novaserver.cutscenes.api.data.Frame;
import xyz.novaserver.cutscenes.api.data.Transition;
import xyz.novaserver.cutscenes.api.data.Vector3f;
import xyz.novaserver.cutscenes.api.data.reader.BlendMCReader;
import xyz.novaserver.cutscenes.api.data.reader.Reader;
import xyz.novaserver.cutscenes.api.data.reader.ReplayReader;
import xyz.novaserver.cutscenes.api.data.reader.YAMLReader;
import xyz.novaserver.cutscenes.paper.CutscenesPaper;
import xyz.novaserver.cutscenes.paper.camera.PaperCamera;
import xyz.novaserver.cutscenes.paper.data.AnimationRecorder;
import xyz.novaserver.cutscenes.paper.data.PaperCutscene;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CutsceneCommand implements TabExecutor {
    private static final CutscenesPaper cutscenes = (CutscenesPaper) Cutscenes.getInstance();
    //private final Map<UUID, AnimationRecorder> recorderMap = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player) || args.length == 0) return true;

        String arg1 = args.length > 1 ? args[1] : null;
        switch (args[0]) {
            case "play" -> play(player, arg1);
            //case "record" -> record(player, arg1);
            //case "stop" -> stop(player);
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
        cutscenes.getLogger().log(Level.SEVERE, msg, ex);
    }

    private Reader parseFilename(String fileName, Player player) {
        Reader reader = null;
        if (fileName.endsWith(".glb")) {
            reader = new ReplayReader();
        } else if (fileName.endsWith(".blendmc")) {
            reader = new BlendMCReader();
        } else if (fileName.endsWith(".yml")) {
            reader = new YAMLReader();
        } else {
            logError("Tried to load an animation that doesn't exist: " + fileName, null, player);
        }
        return reader;
    }

    private void sequence(Player player, String[] args) {
        Location originalLoc = player.getLocation();

        List<Animation> animationList = new ArrayList<>();
        try {
            for (int i = 1; i < args.length; i++) {
                String fileName = args[i];
                Reader reader = parseFilename(fileName, player);
                reader.initialize(new File(cutscenes.getAnimationFolder(), fileName).toPath());
                animationList.add(Animation.fromReader(reader).transition(Transition.fade(20, 0, 20)));
            }
        } catch (IOException e) {
            logError("An IO error was encountered while trying to load animations.", e, player);
        } catch (RuntimeException e) {
            logError("Tried to load an animation that doesn't exist!", null, player);
        }

        if (!animationList.isEmpty()) {
            Camera camera = new PaperCamera(player);
            camera.initialize();
            new PaperCutscene(animationList.toArray(new Animation[0])).play(camera, () -> {
                camera.sendFrame(new Frame(new Vector3f(originalLoc.getX(), originalLoc.getY(), originalLoc.getZ()),
                        originalLoc.getYaw(), originalLoc.getPitch()));
                camera.destroy();
            });
        }
    }

    private void play(Player player, String name) {
        Location originalLoc = player.getLocation();

        Animation animation = null;
        try {
            Reader reader = parseFilename(name, player);
            reader.initialize(new File(cutscenes.getAnimationFolder(), name).toPath());
            animation = Animation.fromReader(reader);
        } catch (IOException e) {
            logError("An IO error was encountered while trying to load " + name + ".yml, does it exist?", e, player);
        } catch (RuntimeException e) {
            logError("Tried to load an animation that doesn't exist!", null, player);
        }

        if (animation != null) {
            Camera camera = new PaperCamera(player);
            camera.initialize();
            new PaperCutscene(animation).play(camera, () -> {
                camera.sendFrame(new Frame(new Vector3f(originalLoc.getX(), originalLoc.getY(), originalLoc.getZ()),
                        originalLoc.getYaw(), originalLoc.getPitch()));
                camera.destroy();
            });
        }
    }

//    private void record(Player player, String name) {
//        UUID uuid = player.getUniqueId();
//        if (recorderMap.containsKey(uuid)) {
//            recorderMap.get(uuid).stop();
//        }
//        AnimationRecorder recorder = new AnimationRecorder(player);
//        recorder.record(name).thenAccept(animation -> {
//            player.sendMessage(Component.text("Finished recording!").color(NamedTextColor.YELLOW));
//            try {
//                animation.saveToFile(new File(CutscenesPaper.getInstance().getAnimationFolder(), animation.name() + ".yml"));
//                player.sendMessage(Component.text("Saved file as " + animation.name() + ".yml").color(NamedTextColor.GREEN));
//            } catch (IOException e) {
//                logError("Failed to save animation to file!", e, player);
//            }
//            recorderMap.remove(uuid);
//        });
//        recorderMap.put(uuid, recorder);
//    }

//    private void stop(Player player) {
//        UUID uuid = player.getUniqueId();
//        if (recorderMap.containsKey(uuid)) {
//            recorderMap.get(uuid).stop();
//            recorderMap.remove(uuid);
//        }
//    }
}
