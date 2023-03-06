package xyz.novaserver.cutscenes.api;

public final class InstanceHolder {
    private static Cutscenes INSTANCE;

    public static void setInstance(Cutscenes instance) {
        INSTANCE = instance;
    }

    public static Cutscenes getInstance() {
        return INSTANCE;
    }
}
