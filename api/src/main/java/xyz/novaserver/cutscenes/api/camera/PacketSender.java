package xyz.novaserver.cutscenes.api.camera;

import java.util.UUID;

public interface PacketSender {

    void spawnEntity(UUID viewer, String entityType, UUID entityUUID, int entityId,
                     double x, double y, double z, float pitch, float yaw);

    void destroyEntity(UUID viewer, int entityId);

    void teleportEntity(UUID viewer, int entityId, double x, double y, double z,
                        float pitch, float yaw);

    void rotateHead(UUID viewer, int entityId, float yaw);

    void setCamera(UUID viewer, int entityId);

    void changeGamemode(UUID viewer, String gameMode);
}
