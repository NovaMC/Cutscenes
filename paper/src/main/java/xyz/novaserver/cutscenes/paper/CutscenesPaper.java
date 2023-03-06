package xyz.novaserver.cutscenes.paper;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.novaserver.cutscenes.api.Cutscenes;
import xyz.novaserver.cutscenes.api.InstanceHolder;
import xyz.novaserver.cutscenes.api.camera.PacketSender;
import xyz.novaserver.cutscenes.paper.camera.PaperPacketSender;
import xyz.novaserver.cutscenes.paper.command.CutsceneCommand;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public final class CutscenesPaper extends JavaPlugin implements Cutscenes {
    private PacketSender packetSender;

    private final File animationFolder = new File(getDataFolder(), "animations");
    private final AtomicInteger ENTITY_COUNTER = new AtomicInteger(Integer.parseInt("5FFFFFFF", 16));

    @Override
    public void onEnable() {
        InstanceHolder.setInstance(this);

        packetSender = new PaperPacketSender();
        saveDefaultConfig();
        if (!getDataFolder().exists()) getDataFolder().mkdirs();
        getServer().getPluginCommand("cutscene").setExecutor(new CutsceneCommand());
    }

    @Override
    public File getAnimationFolder() {
        return animationFolder;
    }

    @Override
    public PacketSender getPacketSender() {
        return packetSender;
    }

    @Override
    public int getNextEntityId() {
        return ENTITY_COUNTER.incrementAndGet();
    }
}
