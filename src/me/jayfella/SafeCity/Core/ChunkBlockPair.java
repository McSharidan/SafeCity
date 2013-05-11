package me.jayfella.SafeCity.Core;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.v1_5_R3.EntityPlayer;
import net.minecraft.server.v1_5_R3.Packet52MultiBlockChange;
import net.minecraft.server.v1_5_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_5_R3.entity.CraftPlayer;

public final class ChunkBlockPair
{
    private final VisualManager visualManager;

    private World chunkWorld;
    private WorldServer nativeChunkWorld;
    private EntityPlayer nativePlayer;

    private int chunkX;
    private int chunkZ;

    private int dirtyCount = 0;
    private byte[] dirtyBlocks = new byte[256];

    private List<ThinLocation> blockChanges = new ArrayList<>();

    public ChunkBlockPair(VisualManager visualManager)
    {
        this.visualManager = visualManager;

        nativePlayer = ((CraftPlayer)visualManager.getSafeCityPlayer().getBukkitPlayer()).getHandle();
    }

    public World getChunkWorld() { return this.chunkWorld; }
    public Chunk getChunk() { return (this.chunkWorld.getChunkAt(chunkX, chunkX)); };
    public int getChunkX() { return this.chunkX; }
    public int getChunkZ() { return this.chunkZ; }

    public synchronized void setData(int x, int z)
    {
        this.chunkWorld = visualManager.getSafeCityPlayer().getBukkitPlayer().getWorld();
        this.nativeChunkWorld = ((org.bukkit.craftbukkit.v1_5_R3.CraftWorld)this.chunkWorld).getHandle();

        this.chunkX = x;
        this.chunkZ = z;

        revertAllBlockChanges();
    }

	public void changeClientBlock(int x, int y, int z, int blockId, int metadata)
    {
        addBlockToPacket(false, x, y, z, blockId, metadata);
    }

    private void addBlockToPacket(boolean isRemoving, int x, int y, int z, int blockId, int metadata)
	{
        if (dirtyCount < 63)
        {
            if (!isRemoving) { this.blockChanges.add(new ThinLocation(x, y, z)); }

            String blockData = String.format("%4s", Integer.toHexString(blockId) + Integer.toHexString(metadata)).replace(' ', '0');

            int index = (dirtyCount * 4);

            dirtyBlocks[index + 0] = (byte)Integer.parseInt(Integer.toHexString(locationToChunkPosition(this.chunkX, x)) + Integer.toHexString(locationToChunkPosition(this.chunkZ, z)), 16);
            dirtyBlocks[index + 1] = (byte)Integer.parseInt(Integer.toHexString(y), 16);
            dirtyBlocks[index + 2] = (byte)Integer.parseInt(blockData.substring(0, 2), 16);
            dirtyBlocks[index + 3] = (byte)Integer.parseInt(blockData.substring(2), 16);

            dirtyCount++;
        }
        else
        {
            sendPacket();

            // back around with a new packet
            addBlockToPacket(isRemoving, x, y, z, blockId, metadata);
        }
    }

    public void revertAllBlockChanges()
    {
        if (blockChanges.isEmpty()) { return; }

        if (this.chunkWorld != null)
        {
            while (!blockChanges.isEmpty())
            {
                ThinLocation loc = blockChanges.get(0);

                Block locBlock = chunkWorld.getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
                int locMaterial = locBlock.getType().getId();

                addBlockToPacket(true, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), locMaterial, locBlock.getData());

                blockChanges.remove(loc);
            }
        }

        // send final packet
        sendPacket();
    }

    private void sendPacket()
    {
        // dont send empty packets
        if (dirtyCount < 1) { return; }

        // create packet
        Packet52MultiBlockChange packet = new Packet52MultiBlockChange(
                chunkX,
                chunkZ,
                new short[64],
                dirtyCount,
                nativeChunkWorld);

        packet.c = dirtyBlocks;

        // send packet
        nativePlayer.playerConnection.sendPacket(packet);

        // reset data
        dirtyCount = 0;
        dirtyBlocks = new byte[256];
    }

    private int locationToChunkPosition(int chunkCoord, int LocationCoord)
    {
        int chuX = chunkCoord << 4;
        int res = LocationCoord - chuX;

        return Math.abs(res);
    }

    public void sendFinalPacket()
    {
        sendPacket();
    }

    public List<ThinLocation> getBlockChanges()
    {
        return this.blockChanges;
    }

}
