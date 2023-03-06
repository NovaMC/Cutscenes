package xyz.novaserver.cutscenes.api.data.reader;

import de.javagl.jgltf.impl.v2.BufferView;
import de.javagl.jgltf.impl.v2.GlTF;
import de.javagl.jgltf.model.io.GltfAssetReader;
import de.javagl.jgltf.model.io.v2.GltfAssetV2;
import xyz.novaserver.cutscenes.api.data.Frame;
import xyz.novaserver.cutscenes.api.util.FileUtils;
import xyz.novaserver.cutscenes.api.util.MathUtils;
import xyz.novaserver.cutscenes.api.data.Vector3f;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;

public class ReplayReader implements Reader {
    private ByteBuffer cameraTranslationBuffer;
    private ByteBuffer cameraRotationBuffer;
    private int totalFrames;
    private String fileName;

    // How many bytes make up a float
    private static final int FLOAT_BYTES = 4;
    // How many float values make up a single coordinate point
    private static final int COORDS_SIZE = 3;
    // How many float values make up a single rotation point
    private static final int ROT_SIZE = 4;

    // The first frame seems to contain incorrect data, so we need to exclude it
    private static final int FRAME_OFFSET = 1;

    @Override
    public Reader initialize(Path fileToRead) throws IOException {
        if (!Files.exists(fileToRead)) {
            throw new RuntimeException("The specified file to read does not exist!");
        }
        GltfAssetV2 asset = (GltfAssetV2) new GltfAssetReader().read(fileToRead);
        GlTF glTF = asset.getGltf();

        fileName = FileUtils.removeExtension(fileToRead.getFileName().toString());
        totalFrames = glTF.getAccessors().get(0).getCount() - FRAME_OFFSET;
        ByteBuffer binaryData = asset.getBinaryData();

        // Allocate buffers for storing our frame data
        cameraTranslationBuffer = ByteBuffer.allocate(FLOAT_BYTES * totalFrames * COORDS_SIZE)
                .order(ByteOrder.LITTLE_ENDIAN);
        cameraRotationBuffer = ByteBuffer.allocate(FLOAT_BYTES * totalFrames * ROT_SIZE)
                .order(ByteOrder.LITTLE_ENDIAN);

        // Grabs the buffer offsets from the glTF file
        BufferView cameraTranslationView = glTF.getBufferViews().get(1);
        BufferView cameraRotationView = glTF.getBufferViews().get(2);

        // Grabs the chunk of data that stores the coordinate data
        cameraTranslationBuffer.put(
                cameraTranslationView.getBuffer(),
                binaryData,
                cameraTranslationView.getByteOffset() + FRAME_OFFSET * FLOAT_BYTES * COORDS_SIZE,
                cameraTranslationView.getByteLength() - FRAME_OFFSET * FLOAT_BYTES * COORDS_SIZE
        );
        // Grabs the chunk of data that store the rotation data
        cameraRotationBuffer.put(
                cameraRotationView.getBuffer(),
                binaryData,
                cameraRotationView.getByteOffset() + FRAME_OFFSET * FLOAT_BYTES * ROT_SIZE,
                cameraRotationView.getByteLength() - FRAME_OFFSET * FLOAT_BYTES * ROT_SIZE
        );

        return this;
    }

    @Override
    public Frame readFrame(int frameDelta) {
        if (frameDelta >= totalFrames) {
            throw new RuntimeException("The frame delta is greater than the total frames.");
        }
        if (cameraTranslationBuffer == null || cameraRotationBuffer == null) {
            throw new RuntimeException("Must initialize before reading frame data!");
        }

        int translationIndex = frameDelta * COORDS_SIZE * FLOAT_BYTES;
        int rotationIndex = frameDelta * ROT_SIZE * FLOAT_BYTES;

        float x = cameraTranslationBuffer.getFloat(translationIndex);
        float y = cameraTranslationBuffer.getFloat(translationIndex + FLOAT_BYTES) - 1.5f; // y values seem to be offset?
        float z = cameraTranslationBuffer.getFloat(translationIndex + FLOAT_BYTES * 2);

        float rotX = cameraRotationBuffer.getFloat(rotationIndex);
        float rotY = cameraRotationBuffer.getFloat(rotationIndex + FLOAT_BYTES);
        float rotZ = cameraRotationBuffer.getFloat(rotationIndex + FLOAT_BYTES * 2);
        float rotW = cameraRotationBuffer.getFloat(rotationIndex + FLOAT_BYTES * 3);

        Vector3f rot = MathUtils.quaternionToVector(rotX, rotY, rotZ, rotW);

        return new Frame(new Vector3f(x, y, z), (float) rot.y(), (float) rot.x());
    }

    @Override
    public int getTotalFrames() {
        return totalFrames;
    }

    @Override
    public String getName() {
        return fileName;
    }
}
