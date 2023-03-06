package xyz.novaserver.cutscenes.api.data;

import xyz.novaserver.cutscenes.api.data.reader.Reader;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Animation implements Cloneable {
    private final String name;
    private final List<Frame> frames;
    private Vector3f startPosition;
    private Transition transition;

    public Animation(String name, List<Frame> frames) {
        this.name = name;
        this.transition = Transition.cut();
        this.startPosition = frames.get(0).position().clone();
        this.frames = frames.stream()
                .peek(f -> f.position().subtract(startPosition))
                .collect(Collectors.toList());
    }

    public String name() {
        return name;
    }

    public List<Frame> frames() {
        return frames;
    }

    public Vector3f startPosition() {
        return startPosition;
    }

    public Animation startPosition(Vector3f startPosition) {
        this.startPosition = startPosition;
        return this;
    }

    public Transition transition() {
        return transition;
    }

    public Animation transition(Transition transition) {
        this.transition = transition;
        return this;
    }


    @Override
    public Animation clone() {
        try {
            return (Animation) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }

    public static Animation fromReader(Reader reader) {
        List<Frame> frameList = new ArrayList<>();
        for (int i = 0; i < reader.getTotalFrames(); i++) {
            frameList.add(reader.readFrame(i));
        }
        return new Animation(reader.getName(), frameList);
    }
}
