package xyz.novaserver.cutscenes.paper.compat;

import xyz.novaserver.cutscenes.paper.compat.listener.CompatListener;
import xyz.novaserver.cutscenes.paper.compat.listener.ThemisListener;
import xyz.novaserver.cutscenes.paper.compat.runner.ActionbarCompat;
import xyz.novaserver.cutscenes.paper.compat.runner.CompatRunner;
import xyz.novaserver.cutscenes.paper.compat.runner.FloodgateCompat;
import xyz.novaserver.cutscenes.paper.compat.runner.TABCompat;

import java.util.HashSet;
import java.util.Set;

public final class CompatHolder<T> {
    public static final Set<CompatHolder<CompatListener>> COMPAT_LISTENERS = new HashSet<>();
    public static final Set<CompatHolder<CompatRunner>> COMPAT_RUNNERS = new HashSet<>();

    static {
        // Compatibility Listeners
        COMPAT_LISTENERS.add(new CompatHolder<>("Themis", ThemisListener.class));

        // Compatibility Runners
        //COMPAT_RUNNERS.add(new CompatHolder<>("NovaPlaceholders", ActionbarCompat.class));
        COMPAT_RUNNERS.add(new CompatHolder<>("floodgate", FloodgateCompat.class));
        //COMPAT_RUNNERS.add(new CompatHolder<>("TAB", TABCompat.class));
    }

    private final String pluginName;
    private final Class<? extends T> clazz;

    public CompatHolder(String pluginName, Class<? extends T> clazz) {
        this.pluginName = pluginName;
        this.clazz = clazz;
    }

    public String getPluginName() {
        return pluginName;
    }

    public Class<? extends T> getClazz() {
        return clazz;
    }
}
