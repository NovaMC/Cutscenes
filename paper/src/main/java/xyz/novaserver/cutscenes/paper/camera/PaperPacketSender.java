package xyz.novaserver.cutscenes.paper.camera;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import xyz.novaserver.cutscenes.api.camera.PacketSender;

import java.util.Collections;
import java.util.UUID;

public class PaperPacketSender implements PacketSender {
    private static final ProtocolManager manager = ProtocolLibrary.getProtocolManager();
    private static final int GAMEMODE_STATE_ID = 3;

    @Override
    public void spawnEntity(UUID viewer, String entityType, UUID entityUUID, int entityId,
                            double x, double y, double z, float pitch, float yaw) {
        Player player = Bukkit.getPlayer(viewer);
        if (player == null || !player.isOnline()) {
            return;
        }
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        packet.getEntityTypeModifier()
                .write(0, EntityType.valueOf(entityType));
        packet.getUUIDs()
                .write(0, entityUUID);
        packet.getIntegers()
                .write(0, entityId) // entityId
                .write(1, 0) // velocity x
                .write(2, 0) // velocity y
                .write(3, 0); // velocity z
        packet.getDoubles()
                .write(0, x) // pos x
                .write(1, y) // pos y
                .write(2, z); // pos z
        packet.getBytes()
                .write(0, (byte)(pitch * 256.0F / 360.0F)) // xRot
                .write(1, (byte)(yaw * 256.0F / 360.0F)) // yRot
                .write(2, (byte)(yaw * 256.0F / 360.0F)); // head yRot
        manager.sendServerPacket(player, packet);
    }

    @Override
    public void destroyEntity(UUID viewer, int entityId) {
        Player player = Bukkit.getPlayer(viewer);
        if (player == null || !player.isOnline()) {
            return;
        }
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        packet.getIntLists()
                .write(0, Collections.singletonList(entityId));
        manager.sendServerPacket(player, packet);
    }

    @Override
    public void teleportEntity(UUID viewer, int entityId, double x, double y, double z,
                               float pitch, float yaw) {
        Player player = Bukkit.getPlayer(viewer);
        if (player == null || !player.isOnline()) {
            return;
        }
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);
        packet.getIntegers()
                .write(0, entityId);
        packet.getDoubles()
                .write(0, x)
                .write(1, y)
                .write(2, z);
        packet.getBytes()
                .write(0, (byte)(yaw * 256.0F / 360.0F)) // yRot
                .write(1, (byte)(pitch * 256.0F / 360.0F)); // xRot
        packet.getBooleans()
                .write(0, false); // onGround
        manager.sendServerPacket(player, packet);
    }

    @Override
    public void rotateHead(UUID viewer, int entityId, float yaw) {
        Player player = Bukkit.getPlayer(viewer);
        if (player == null || !player.isOnline()) {
            return;
        }
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
        packet.getIntegers()
                .write(0, entityId);
        packet.getBytes()
                .write(0, (byte)(yaw * 256.0F / 360.0F)); // yHeadRot
        manager.sendServerPacket(player, packet);
    }

    @Override
    public void setCamera(UUID viewer, int entityId) {
        Player player = Bukkit.getPlayer(viewer);
        if (player == null || !player.isOnline()) {
            return;
        }
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.CAMERA);
        packet.getIntegers()
                .write(0, entityId);
        manager.sendServerPacket(player, packet);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void changeGamemode(UUID viewer, String gameMode) {
        Player player = Bukkit.getPlayer(viewer);
        if (player == null || !player.isOnline()) {
            return;
        }
        PacketContainer statePacket = new PacketContainer(PacketType.Play.Server.GAME_STATE_CHANGE);
        statePacket.getGameStateIDs()
                .write(0, GAMEMODE_STATE_ID);
        statePacket.getFloat()
                .write(0, (float) GameMode.valueOf(gameMode).getValue());

        // Grabs the player's playerlist name and converts it for use in the packet
        WrappedChatComponent wrappedName = WrappedChatComponent
                .fromJson(GsonComponentSerializer.gson().serialize(player.playerListName()));

        PacketContainer infoPacket = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
        infoPacket.getPlayerInfoAction()
                .write(0, EnumWrappers.PlayerInfoAction.UPDATE_GAME_MODE);
        infoPacket.getPlayerInfoDataLists()
                .write(0, Collections.singletonList(new PlayerInfoData(
                        WrappedGameProfile.fromPlayer(player),
                        player.getPing(),
                        EnumWrappers.NativeGameMode.valueOf(gameMode),
                        wrappedName
                )));

        manager.sendServerPacket(player, statePacket);
        manager.sendServerPacket(player, infoPacket);
    }
}
