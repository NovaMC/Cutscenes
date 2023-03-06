package xyz.novaserver.cutscenes.api;

import xyz.novaserver.cutscenes.api.camera.PacketSender;

import java.io.File;

public interface Cutscenes {
    static Cutscenes getInstance() {
        return InstanceHolder.getInstance();
    }

    File getAnimationFolder();

    PacketSender getPacketSender();

    int getNextEntityId();

}
