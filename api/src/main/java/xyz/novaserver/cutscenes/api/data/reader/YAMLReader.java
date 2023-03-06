package xyz.novaserver.cutscenes.api.data.reader;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import xyz.novaserver.cutscenes.api.data.Frame;
import xyz.novaserver.cutscenes.api.data.Vector3f;
import xyz.novaserver.cutscenes.api.util.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class YAMLReader implements Reader {
    private Vector3f defaultPos;
    private List<ConfigurationNode> frameNodes;
    private String fileName;

    @Override
    public Reader initialize(Path fileToRead) throws IOException {
        if (!Files.exists(fileToRead)) {
            throw new RuntimeException("The specified file to read does not exist!");
        }
        fileName = FileUtils.removeExtension(fileToRead.getFileName().toString());
        frameNodes = new ArrayList<>();

        YamlConfigurationLoader loader = YamlConfigurationLoader.builder().path(fileToRead).build();
        ConfigurationNode rootNode = loader.load();

        defaultPos = new Vector3f(
                rootNode.node("default-pos", "x").getDouble(),
                rootNode.node("default-pos", "y").getDouble(),
                rootNode.node("default-pos", "z").getDouble()
        );

        frameNodes.addAll(rootNode.node("frames").childrenMap().values());

        return this;
    }

    @Override
    public Frame readFrame(int frameDelta) {
        if (frameDelta >= frameNodes.size()) {
            throw new RuntimeException("The frame delta is greater than the total frames.");
        }
        if (defaultPos == null || frameNodes.isEmpty()) {
            throw new RuntimeException("Must initialize before reading frame data!");
        }
        ConfigurationNode node = frameNodes.get(frameDelta);

        double x = node.node("x").getDouble();
        double y = node.node("y").getDouble();
        double z = node.node("z").getDouble();
        float yaw = node.node("yaw").getFloat();
        float pitch = node.node("pitch").getFloat();

        x += defaultPos.x();
        y += defaultPos.y();
        z += defaultPos.z();

        return new Frame(new Vector3f(x, y, z), yaw, pitch);
    }

    @Override
    public int getTotalFrames() {
        return frameNodes.size();
    }

    @Override
    public String getName() {
        return fileName;
    }
}
