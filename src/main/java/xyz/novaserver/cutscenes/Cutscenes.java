package xyz.novaserver.cutscenes;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.novaserver.cutscenes.command.CutsceneCommand;

import java.io.File;

public final class Cutscenes extends JavaPlugin {
    private final File animationFolder = new File(getDataFolder(), "animations");
    private static Cutscenes instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        if (!getDataFolder().exists()) getDataFolder().mkdirs();
        getServer().getPluginCommand("cutscene").setExecutor(new CutsceneCommand());
    }

    public File getAnimationFolder() {
        return animationFolder;
    }

    public static Cutscenes getInstance() {
        return instance;
    }
}
