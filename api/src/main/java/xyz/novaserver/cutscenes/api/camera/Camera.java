package xyz.novaserver.cutscenes.api.camera;

import xyz.novaserver.cutscenes.api.data.Frame;
import xyz.novaserver.cutscenes.api.data.Transition;

public interface Camera {

    void initialize();

    void destroy();

    void sendFrame(Frame frame);

    void sendTransition(Transition transition);

    void sceneChange(Frame frame);

}
