package me.jayfella.SafeCity.Runnables;

import java.util.ArrayList;
import me.jayfella.SafeCity.Core.ChunkBlockPair;
import me.jayfella.SafeCity.Core.ThinLocation;
import me.jayfella.SafeCity.Core.VisualManager;
import org.bukkit.Chunk;
import org.bukkit.World;

public class ZoneCornerHighlighter implements Runnable
{
    private VisualManager visualManager;

    public ZoneCornerHighlighter(VisualManager visualManager)
    {
        this.visualManager = visualManager;
    }

    public void visualize()
    {
        visualManager.getContext().getPlugin().getServer().getScheduler().runTaskAsynchronously(visualManager.getContext().getPlugin(), this);
    }

    @Override
    public void run()
    {
        visualManager.clearVisuals(false);

        ArrayList<ThinLocation> zoneEdges = getCurrentZone();
        displayCurrentZone(zoneEdges);
    }

    private ArrayList<ThinLocation> getCurrentZone()
    {
        // set all visible chunks
        Chunk playerChunk = visualManager.getSafeCityPlayer().getBukkitPlayer().getLocation().getChunk();

		int currentChunkX = playerChunk.getX();
		int currentChunkZ = playerChunk.getZ();

        int serverViewDistance = visualManager.getServerViewDistance();

        int count = 0;

		for (int x = (currentChunkX - serverViewDistance); x <= (currentChunkX + serverViewDistance); x++)
		{
			for (int z = (currentChunkZ - serverViewDistance); z <= (currentChunkZ + serverViewDistance); z++)
			{
                visualManager.getActiveVisuals()[count].setData(x, z);
                count++;
			}
		}

        int minX = ((currentChunkX - serverViewDistance) << 4);
		int maxX = ((currentChunkX + serverViewDistance) << 4);

		int minZ = ((currentChunkZ - serverViewDistance) << 4);
		int maxZ = ((currentChunkZ + serverViewDistance) << 4);

        ThinLocation areaLesserCorner = new ThinLocation(minX, 45, minZ);
		ThinLocation areaGreaterCorner = new ThinLocation(maxX, 45, maxZ);

        if ((visualManager.getSafeCityPlayer().getZoneManager().getCurrentSubZone() == null) & (visualManager.getSafeCityPlayer().getZoneManager().getCurrentZone() != null))
        {
            // return visualManager.getCornerLocations(visualManager.getSafeCityPlayer().getZoneManager().getCurrentZone().getLesserCorner(), visualManager.getSafeCityPlayer().getZoneManager().getCurrentZone().getGreaterCorner(), areaLesserCorner, areaGreaterCorner);
        }
        else if (visualManager.getSafeCityPlayer().getZoneManager().getCurrentSubZone() != null)
        {
            // return visualManager.getCornerLocations(visualManager.getSafeCityPlayer().getZoneManager().getCurrentSubZone().getLesserCorner(), visualManager.getSafeCityPlayer().getZoneManager().getCurrentSubZone().getGreaterCorner(), areaLesserCorner, areaGreaterCorner);
        }

        return new ArrayList<>();

    }

    private void displayCurrentZone(ArrayList<ThinLocation> edges)
    {
        World currentWorld = visualManager.getSafeCityPlayer().getBukkitPlayer().getWorld();

        for (ThinLocation loc : edges)
        {
            for (ChunkBlockPair c : visualManager.getActiveVisuals())
            {
                Chunk locChunk = currentWorld.getChunkAt(loc.getBlockX() >> 4, loc.getBlockZ() >> 4);

                int chunkX = locChunk.getX();
                int chunkZ = locChunk.getZ();

                boolean isChunkX = (chunkX == c.getChunkX());
                boolean isChunkZ = (chunkZ == c.getChunkZ());

                if (isChunkX && isChunkZ)
                {
                    if (loc.getBlockY() == 1 && loc.getBlockY() == 255)
                    {
                        c.changeClientBlock(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), visualManager.getZoneMaterial(), (byte)9);
                    }
                    else
                    {
                        c.changeClientBlock(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), visualManager.getZoneMaterial(), (byte)9);
                    }

                    break;
                }
            }
        }

		for (ChunkBlockPair c : visualManager.getActiveVisuals())
        {
            c.sendFinalPacket();
        }

    }

}
