package xyz.novaserver.cutscenes.api.camera;

import java.util.UUID;

public class FakeEntity {
    private final int entityId;
    private final UUID entityUUID;

    public FakeEntity(int entityId, UUID entityUUID) {
        this.entityId = entityId;
        this.entityUUID = entityUUID;
    }

    public int getEntityId() {
        return entityId;
    }

    public UUID getEntityUUID() {
        return entityUUID;
    }
}
