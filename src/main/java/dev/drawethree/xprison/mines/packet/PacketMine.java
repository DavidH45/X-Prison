package dev.drawethree.xprison.mines.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a player's personal mine rendered client side using packets.
 */
public class PacketMine {

    private final UUID owner;
    private final int size;
    private final int height = 64;
    private final Location origin;
    private final Set<BlockPosition> brokenBlocks = new HashSet<>();
    private final Material blockMaterial = Material.STONE;

    public PacketMine(UUID owner, Location origin, int size) {
        this.owner = owner;
        this.origin = origin;
        this.size = size;
    }

    public UUID getOwner() {
        return owner;
    }

    public int getSize() {
        return size;
    }

    public Location getOrigin() {
        return origin;
    }

    private ProtocolManager protocol() {
        return ProtocolLibrary.getProtocolManager();
    }

    /**
     * Sends packets to the player rendering the full mine.
     */
    public void render(Player player) {
        ProtocolManager pm = protocol();
        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                for (int y = 0; y < height; y++) {
                    BlockPosition bp = new BlockPosition(
                            origin.getBlockX() + x,
                            origin.getBlockY() + y,
                            origin.getBlockZ() + z);
                    PacketContainer packet = pm.createPacket(PacketType.Play.Server.BLOCK_CHANGE);
                    packet.getBlockPositionModifier().write(0, bp);
                    packet.getBlockData().write(0, WrappedBlockData.createData(blockMaterial));
                    try {
                        pm.sendServerPacket(player, packet);
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }

    /**
     * Checks if given position resides within mine bounds.
     */
    public boolean isInside(BlockPosition pos) {
        int minX = origin.getBlockX();
        int minY = origin.getBlockY();
        int minZ = origin.getBlockZ();
        return pos.getX() >= minX && pos.getX() < minX + size
                && pos.getY() >= minY && pos.getY() < minY + height
                && pos.getZ() >= minZ && pos.getZ() < minZ + size;
    }

    /**
     * Handles block breaking by sending air packet and tracking progress.
     */
    public void handleBreak(Player player, BlockPosition pos) {
        if (!brokenBlocks.add(pos)) {
            return;
        }

        ProtocolManager pm = protocol();
        PacketContainer packet = pm.createPacket(PacketType.Play.Server.BLOCK_CHANGE);
        packet.getBlockPositionModifier().write(0, pos);
        packet.getBlockData().write(0, WrappedBlockData.createData(Material.AIR));
        try {
            pm.sendServerPacket(player, packet);
        } catch (Exception ignored) {
        }
    }

    public double getProgress() {
        return (double) brokenBlocks.size() / (size * size * height);
    }

    /**
     * Resets the mine back to original blocks.
     */
    public void reset(Player player) {
        brokenBlocks.clear();
        render(player);
    }
}
