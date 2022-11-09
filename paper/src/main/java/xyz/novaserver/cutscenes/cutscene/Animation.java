package xyz.novaserver.cutscenes.cutscene;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Animation {
    private final String name;
    private final List<Frame> frames;
    private Vector startPosition;
    private Transition transition;

    public Animation(String name, List<Frame> frames) {
        this.name = name;
        this.transition = Transition.cut();
        this.startPosition = frames.get(0).getPosition().clone();
        this.frames = frames.stream().peek(f -> f.getPosition()
                        .subtract(startPosition))
                .collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public List<Frame> getFrames() {
        return frames;
    }

    public Vector getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(Vector startPosition) {
        this.startPosition = startPosition;
    }

    public Transition getTransition() {
        return transition;
    }

    public void setTransition(Transition transition) {
        this.transition = transition;
    }

    public Animation copy() {
        Animation animation = new Animation(name, new ArrayList<>(frames));
        animation.setStartPosition(startPosition);
        animation.setTransition(transition);
        return animation;
    }

    public void saveToFile(File file) throws IOException {
        YamlConfiguration yaml = new YamlConfiguration();

        // Save default location
        yaml.set("default-pos.x", startPosition.getX());
        yaml.set("default-pos.y", startPosition.getY());
        yaml.set("default-pos.z", startPosition.getZ());

        // Save frames
        frames.forEach(f -> {
            ConfigurationSection section = yaml.createSection("frames." + frames.indexOf(f));
            section.set("x", f.getPosition().getX());
            section.set("y", f.getPosition().getY());
            section.set("z", f.getPosition().getZ());
            section.set("yaw", f.getYaw());
            section.set("pitch", f.getPitch());
        });

        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        System.out.println("Trying to save file: " + file.getName());
        yaml.save(file);
    }

    public static Animation parse(File file) throws IOException {
        YamlConfiguration yaml = new YamlConfiguration();
        List<Frame> frames = new ArrayList<>();

        try {
            System.out.println("Trying to read file: " + file.getName());
            yaml.load(file);
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }

        Vector defPos = new Vector(
                yaml.getDouble("default-pos.x"),
                yaml.getDouble("default-pos.y"),
                yaml.getDouble("default-pos.z"));

        yaml.getConfigurationSection("frames").getKeys(false).stream()
                .map(s -> yaml.getConfigurationSection("frames." + s))
                .forEach(section -> frames.add(new Frame(new Vector(
                        section.getDouble("x") + defPos.getX(),
                        section.getDouble("y") + defPos.getY(),
                        section.getDouble("z") + defPos.getZ()),
                        (float) section.getDouble("yaw"),
                        (float) section.getDouble("pitch"))));

        return new Animation(file.getName().replace(".yml", ""), frames);
    }
}
