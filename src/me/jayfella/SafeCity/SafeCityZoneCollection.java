package me.jayfella.SafeCity;

import java.util.ArrayList;
import me.jayfella.SafeCity.Core.ChunkLocation;
import me.jayfella.SafeCity.Core.ThinLocation;
import org.bukkit.Location;
import org.bukkit.World;

public final class SafeCityZoneCollection
{
    private final ChunkLocation chunkLocation;
    private final ArrayList<SafeCityZone> zones;

    public SafeCityZoneCollection(ChunkLocation chunkLoc)
    {
        this.chunkLocation = chunkLoc;
        this.zones = new ArrayList<>();
    }

    public int getChunkX() { return this.chunkLocation.getChunkX(); }
    public int getChunkZ() { return this.chunkLocation.getChunkZ(); }

    public SafeCityZone[] getAllZones() { return this.zones.toArray(new SafeCityZone[zones.size()]); }

    public SafeCityZone getZone(int zoneId)
    {
        for (int i = 0; i < zones.size(); i++)
        {
            if (zones.get(i).getId() == zoneId)
            {
                return zones.get(i);
            }
        }

        return null;
    }

    public void addZone(SafeCityZone newZone) { zones.add(newZone); }

    public void removeZone(SafeCityZone zone)
    {
        zones.remove(zone);
    }

    public boolean containsZone(SafeCityZone newZone)
    {
        for (SafeCityZone zone : zones)
        {
            if (zone.equals(newZone))
            {
                return true;
            }
        }

        return false;
    }



    public SafeCityZone getExactZone(Location location)
    {
        World world = location.getWorld();
        ThinLocation loc = new ThinLocation(location.getBlockX(), location.getBlockY(), location.getBlockZ());

        for (SafeCityZone zone : zones)
        {
            if (zone.isInsideZone(loc, world, false))
            {
                return zone;
            }
        }

        return null;
    }

    public SafeCityZone getExactZone(ThinLocation location, World world)
    {
        return getExactZone(new Location(world, location.getBlockX(), location.getBlockY(), location.getBlockZ()));
    }

}
