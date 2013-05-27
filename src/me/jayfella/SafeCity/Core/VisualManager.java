package me.jayfella.SafeCity.Core;

import java.util.ArrayList;
import java.util.List;
import me.jayfella.SafeCity.Runnables.ZoneCornerHighlighter;
import me.jayfella.SafeCity.Runnables.ZoneVisualRemover;
import me.jayfella.SafeCity.Runnables.ZoneVisualizer;
import me.jayfella.SafeCity.SafeCityContext;
import me.jayfella.SafeCity.SafeCityPlayer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public final class VisualManager
{
    private final SafeCityContext context;
    private final SafeCityPlayer scPlayer;

    private final ZoneVisualizer zoneVisualizer;
    private final ZoneCornerHighlighter zoneHighlighter;
    private final ZoneVisualRemover visualRemover;

    private final int serverViewDistance;
    private final ChunkBlockPair[] activeVisuals;

    private final int zoneMaterial = Material.WOOL.getId();

    private final int zoneNormalData = 11; // white
    private final int zoneForSaleData = 13; // lime green
    private final int zoneForRentData = 5; // lime green
    private final int zoneSoldData = 14; // dark red
    private final int zoneRentedData = 1; // dark red

    public VisualManager(SafeCityContext context, SafeCityPlayer scPlayer)
    {
	this.context = context;
	this.scPlayer = scPlayer;

        this.serverViewDistance = this.getContext().getPlugin().getServer().getViewDistance();

	this.zoneVisualizer = new ZoneVisualizer(this);
        this.zoneHighlighter = new ZoneCornerHighlighter(this);
	this.visualRemover = new ZoneVisualRemover(this);

        int width = (serverViewDistance * 2) + 1;
        int length = (serverViewDistance * 2) + 1;
        int totalSize = (width * length);

        activeVisuals = new ChunkBlockPair[totalSize];

        for (int i = 0; i < activeVisuals.length; i++)
        {
            activeVisuals[i] = new ChunkBlockPair(this);
        }
    }

    public SafeCityContext getContext()	{ return this.context; }
    public SafeCityPlayer getSafeCityPlayer() {	return this.scPlayer; }

    public ThinLocation getVisibleLocation(ThinLocation location)
    {
	Block block = scPlayer.getBukkitPlayer().getWorld().getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
	BlockFace direction = isTransparent(block) ? BlockFace.DOWN : BlockFace.UP;

        int maxHeight = scPlayer.getBukkitPlayer().getWorld().getMaxHeight();

	while ((block.getY() >= 1) && (block.getY() < maxHeight - 1) && ((!isTransparent(block.getRelative(BlockFace.UP))) || (isTransparent(block))))
	{
            block = block.getRelative(direction);
        }

	   return new ThinLocation(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
    }

    private boolean isTransparent(Block block)
    {
        /*return (block.getType() == Material.AIR) ||
                (block.getType() == Material.LONG_GRASS) ||
		(block.getType() == Material.FENCE) ||
		(block.getType() == Material.CHEST) ||
		(block.getType() == Material.TORCH) ||
		(block.getType() == Material.VINE) ||
		(block.getType() == Material.LEAVES) ||
		(block.getType() == Material.RED_ROSE) ||
		(block.getType() == Material.YELLOW_FLOWER);*/
        
        return !block.getType().isSolid();
    }

    public void clearVisuals(boolean runAsync) { this.getVisualRemover().removeVisuals(runAsync); }
    public ZoneVisualizer getZoneVisualizer() { return this.zoneVisualizer; }
    public ZoneCornerHighlighter getZoneHighlighter() { return this.zoneHighlighter; }
    public ZoneVisualRemover getVisualRemover() { return this.visualRemover; }
    public ChunkBlockPair[] getActiveVisuals() { return this.activeVisuals; }
    public int getServerViewDistance() { return this.serverViewDistance; }
    public int getZoneMaterial() { return this.zoneMaterial; }
    public int getZoneNormalData() { return this.zoneNormalData; }
    public int getZoneForSaleData() { return this.zoneForSaleData; }
    public int getZoneForRentData() { return this.zoneForRentData; }
    public int getZoneSoldData() { return this.zoneSoldData; }
    public int getZoneRentedData() { return this.zoneRentedData; }

    public List<ThinLocation> getZoneEdgesFromChunk(ThinLocation zoneLesserCorner, ThinLocation zoneGreaterCorner, ThinLocation chunkLesserCorner, ThinLocation chunkGreaterCorner)
    {
        List<ThinLocation> edges = new ArrayList<>();

        // check whether this houses any edges
        boolean drawFrontX = (zoneLesserCorner.getBlockZ() >= chunkLesserCorner.getBlockZ() && zoneLesserCorner.getBlockZ() <= chunkGreaterCorner.getBlockZ());
        boolean drawBackX = (zoneGreaterCorner.getBlockZ() >= chunkLesserCorner.getBlockZ() && zoneGreaterCorner.getBlockZ() <= chunkGreaterCorner.getBlockZ());

        boolean drawLeftZ = (zoneLesserCorner.getBlockX() >= chunkLesserCorner.getBlockX() && zoneLesserCorner.getBlockX() <= chunkGreaterCorner.getBlockX());
        boolean drawRightZ = (zoneGreaterCorner.getBlockX() >= chunkLesserCorner.getBlockX() && zoneGreaterCorner.getBlockX() <= chunkGreaterCorner.getBlockX());

        // if no visible edges, return;
        if (!drawFrontX && !drawBackX && !drawLeftZ && !drawRightZ) { return edges; }

        boolean is2dZone = (zoneLesserCorner.getBlockY() == 1 && zoneGreaterCorner.getBlockY() == 255);

        // used for 3D zones
        int yBottom = zoneLesserCorner.getBlockY();
        int yTop = zoneGreaterCorner.getBlockY();

        if (drawFrontX | drawBackX)
        {
            int startX = (zoneLesserCorner.getBlockX() > chunkLesserCorner.getBlockX()) ? zoneLesserCorner.getBlockX() : chunkLesserCorner.getBlockX();
            int endX = (zoneGreaterCorner.getBlockX() < chunkGreaterCorner.getBlockX()) ? zoneGreaterCorner.getBlockX() : chunkGreaterCorner.getBlockX();

            int frontZ = zoneLesserCorner.getBlockZ();
            int backZ = zoneGreaterCorner.getBlockZ();

            for (int x = startX; x <= endX; x++)
            {
                if ((x & 1) == 1) { continue; } // checkerboard

                if (is2dZone)
                {
                    if (drawFrontX) { edges.add(this.getVisibleLocation(new ThinLocation(x, scPlayer.getBukkitPlayer().getWorld().getHighestBlockYAt(x, frontZ), frontZ))); }
                    if (drawBackX) { edges.add(this.getVisibleLocation(new ThinLocation(x, scPlayer.getBukkitPlayer().getWorld().getHighestBlockYAt(x, backZ), backZ))); }
                }
                else
                {
                    if (drawFrontX)
                    {
                        edges.add(new ThinLocation(x, yBottom, frontZ));
                        edges.add(new ThinLocation(x, yTop, frontZ));
                    }

                    if (drawBackX)
                    {
                        edges.add(new ThinLocation(x, yBottom, backZ));
                        edges.add(new ThinLocation(x, yTop, backZ));
                    }
                }
            }
        }

        if (drawLeftZ | drawRightZ)
        {
            int startZ = (zoneLesserCorner.getBlockZ() > chunkLesserCorner.getBlockZ()) ? zoneLesserCorner.getBlockZ() : chunkLesserCorner.getBlockZ();
            int endZ = (zoneGreaterCorner.getBlockZ() < chunkGreaterCorner.getBlockZ()) ? zoneGreaterCorner.getBlockZ() : chunkGreaterCorner.getBlockZ();

            int leftX = zoneLesserCorner.getBlockX();
            int rightX = zoneGreaterCorner.getBlockX();

            for (int z = startZ; z <= endZ; z++)
            {
                if ((z & 1) == 1) { continue; } // checkerboard

                if (is2dZone)
                {
                    if (drawLeftZ) { edges.add(this.getVisibleLocation(new ThinLocation(leftX, scPlayer.getBukkitPlayer().getWorld().getHighestBlockYAt(leftX, z), z))); }
                    if (drawRightZ) { edges.add(this.getVisibleLocation(new ThinLocation(rightX, scPlayer.getBukkitPlayer().getWorld().getHighestBlockYAt(rightX, z), z))); }
                }
                else
                {
                    if (drawLeftZ)
                    {
                        edges.add(new ThinLocation(leftX, yBottom, z));
                        edges.add(new ThinLocation(leftX, yTop, z));
                    }

                    if (drawRightZ)
                    {
                        edges.add(new ThinLocation(rightX, yBottom, z));
                        edges.add(new ThinLocation(rightX, yTop, z));
                    }
                }
            }
        }

        // corners
        if (!is2dZone)
        {
            // if any of the corners are in this chunk
            if ((drawFrontX && drawLeftZ) | (drawFrontX && drawRightZ) | (drawBackX && drawLeftZ) | (drawBackX && drawRightZ))
            {
                for (int y = yBottom; y <= yTop; y++)
                {
                    if ((y & 1) != 1) { continue; } // checkerboard

                    if (drawFrontX && drawLeftZ) { edges.add(new ThinLocation(zoneLesserCorner.getBlockX(), y, zoneLesserCorner.getBlockZ())); }
                    if (drawFrontX && drawRightZ) { edges.add(new ThinLocation(zoneGreaterCorner.getBlockX(), y, zoneLesserCorner.getBlockZ())); }
                    if (drawBackX && drawLeftZ) { edges.add(new ThinLocation(zoneLesserCorner.getBlockX(), y, zoneGreaterCorner.getBlockZ())); }
                    if (drawBackX && drawRightZ) { edges.add(new ThinLocation(zoneGreaterCorner.getBlockX(), y, zoneGreaterCorner.getBlockZ())); }
                }
            }
        }

        return edges;
    }

}
