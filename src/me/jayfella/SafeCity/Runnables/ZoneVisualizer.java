package me.jayfella.SafeCity.Runnables;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import me.jayfella.SafeCity.Core.ChunkBlockPair;
import me.jayfella.SafeCity.Core.ThinLocation;
import me.jayfella.SafeCity.Core.VisualManager;
import me.jayfella.SafeCity.SafeCitySubZone;
import me.jayfella.SafeCity.SafeCitySubZoneCollection;
import me.jayfella.SafeCity.SafeCityZone;
import me.jayfella.SafeCity.SafeCityZoneCollection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;

public class ZoneVisualizer implements Runnable
{
    private VisualManager visualManager;

    private boolean isCoolingDown = false;

    public ZoneVisualizer(VisualManager visualManager)
    {
        this.visualManager = visualManager;
    }

    public void visualize()
    {
        if (isCoolingDown)
        {
            visualManager.getSafeCityPlayer().getBukkitPlayer().sendMessage(ChatColor.RED + "Visualizer is cooling down.");
            return;
        }

        isCoolingDown = true;
        visualManager.getContext().getPlugin().getServer().getScheduler().runTaskAsynchronously(visualManager.getContext().getPlugin(), this).getTaskId();
    }

    @Override
    public void run()
    {
        // set all visible chunks
        Chunk playerChunk = visualManager.getSafeCityPlayer().getBukkitPlayer().getLocation().getChunk();

        int currentChunkX = playerChunk.getX();
        int currentChunkZ = playerChunk.getZ();

        int serverViewDistance = visualManager.getServerViewDistance();

        int minViewChunkX = (currentChunkX - serverViewDistance);
        int maxViewChunkX = (currentChunkX + serverViewDistance);

        int minViewChunkZ = (currentChunkZ - serverViewDistance);
        int maxViewChunkZ = (currentChunkZ + serverViewDistance);

        int count = 0;

        for (int x = minViewChunkX; x <= maxViewChunkX; x++)
        {
            for (int z = minViewChunkZ; z <= maxViewChunkZ; z++)
            {
                ChunkBlockPair cbp = visualManager.getActiveVisuals()[count];
                
                // needs to be synchronous
                cbp.setData(x, z);
                
                int minX = x << 4;
                int minZ = z << 4;

                int maxX = minX + 15;
                int maxZ = minZ + 15;

                ThinLocation chunkLesserCorner = new ThinLocation(minX, 45, minZ);
                ThinLocation chunkGreaterCorner = new ThinLocation(maxX, 45, maxZ);

                SafeCityZoneCollection zoneColl = visualManager.getContext().getZonesInChunk(x, z);
                if (zoneColl != null)
                {
                    for (int i = 0; i < zoneColl.getAllZones().length; i++)
                    {
                        SafeCityZone zone = zoneColl.getAllZones()[i];

                        int matByte;

                        if (zone.isForRent()) { matByte = visualManager.getZoneForRentData(); }
                        else if (zone.isRented()) { matByte = visualManager.getZoneRentedData(); }
                        else if (zone.isForSale()) { matByte = visualManager.getZoneForSaleData(); }
                        else { matByte = visualManager.getZoneNormalData(); }

                        List<ThinLocation> edges = visualManager.getZoneEdgesFromChunk(zone.getLesserCorner(), zone.getGreaterCorner(), chunkLesserCorner, chunkGreaterCorner);

                        if (!edges.isEmpty())
                        {
                            for (ThinLocation loc : edges)
                            {
                                cbp.changeClientBlock(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), visualManager.getZoneMaterial(), matByte);
                            }
                        }
                    }
                }

                SafeCitySubZoneCollection subZoneColl = visualManager.getContext().getSubZonesInChunk(x, z);
                if (subZoneColl != null)
                {
                    for (int i = 0; i < subZoneColl.getAllSubZones().length; i++)
                    {
                        SafeCitySubZone subZone = subZoneColl.getAllSubZones()[i];

                        List<ThinLocation> edges = visualManager.getZoneEdgesFromChunk(subZone.getLesserCorner(), subZone.getGreaterCorner(), chunkLesserCorner, chunkGreaterCorner);

                        int matByte;
                        if (subZone.isForRent()) { matByte = visualManager.getZoneForRentData(); }
                        else if (subZone.isRented()) { matByte = visualManager.getZoneRentedData(); }
                        else if (subZone.isForSale()) { matByte = visualManager.getZoneForSaleData(); }
                        else if (subZone.isSold()) { matByte = visualManager.getZoneSoldData(); }
                        else {  matByte = visualManager.getZoneNormalData(); }

                        if (!edges.isEmpty())
                        {
                            for (ThinLocation loc : edges)
                            {
                                cbp.changeClientBlock(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), visualManager.getZoneMaterial(), matByte);
                            }
                        }
                    }
                }

                count++;
			}
            }

        // send all the final unfilled packets...
        for (ChunkBlockPair c : visualManager.getActiveVisuals()) { c.sendFinalPacket(); }

        // stop people from spamming the visualizer
        if (!visualManager.getSafeCityPlayer().getBukkitPlayer().isOp())
        {
            initiateCooldown(100L);
        }
        else
        {
            initiateCooldown(20L);
        }
    }


    private void initiateCooldown(long ticks)
    {
        visualManager.getContext().getPlugin().getServer().getScheduler().runTaskLater(visualManager.getContext().getPlugin(),
            new Runnable()
            {
                @Override
                public void run()
                {
                    isCoolingDown = false;
                }
            },
            ticks);
    }

}

