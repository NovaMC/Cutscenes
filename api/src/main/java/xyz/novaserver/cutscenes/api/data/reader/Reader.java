package xyz.novaserver.cutscenes.api.data.reader;

import xyz.novaserver.cutscenes.api.data.Frame;

import java.io.IOException;
import java.nio.file.Path;

public interface Reader {

    /**
     * Initializes the reader by reading the frame data from the file
     * and storing the frames for use with getFrame()
     */
    Reader initialize(Path fileToRead) throws IOException;

    /**
     * Creates a new frame directly from the frame data (slow)
     * Should probably use getFrame() instead
     * @param frameDelta The index of the frame to get
     * @return A frame fom this classes' frame data
     */
    Frame readFrame(int frameDelta);


    /**
     * @return The total number of frames in this file
     */
    int getTotalFrames();

    /**
     * @return The formatted name of the file this Reader was initialized with
     */
    String getName();

}
