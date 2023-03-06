package xyz.novaserver.cutscenes.api.data.reader;

import xyz.novaserver.cutscenes.api.data.Frame;

import java.io.IOException;
import java.nio.file.Path;

public class BlendMCReader implements Reader {
    @Override
    public Reader initialize(Path fileToRead) throws IOException {
        return null;
    }

    @Override
    public Frame readFrame(int frameDelta) {
        return null;
    }

    @Override
    public int getTotalFrames() {
        return 0;
    }

    @Override
    public String getName() {
        return null;
    }
}
