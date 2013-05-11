package me.jayfella.SafeCity;

import java.util.ArrayList;
import me.jayfella.SafeCity.Core.ChunkLocation;
import me.jayfella.SafeCity.Core.ThinLocation;
import org.bukkit.Location;
import org.bukkit.World;

public final class SafeCitySubZoneCollection
{
    private final ChunkLocation chunkLocation;
    private final ArrayList<SafeCitySubZone> subZones;

    public SafeCitySubZoneCollection(ChunkLocation chunkLoc)
    {
        this.chunkLocation = chunkLoc;
        this.subZones = new ArrayList<>();
    }

    public int getChunkX() { return this.chunkLocation.getChunkX(); }
    public int getChunkZ() { return this.chunkLocation.getChunkZ(); }

    public SafeCitySubZone[] getAllSubZones() { return this.subZones.toArray(new SafeCitySubZone[subZones.size()]); }

    public SafeCitySubZone getSubZone(int zoneId)
    {
        for (int i = 0; i < subZones.size(); i++)
        {
            if (subZones.get(i).getId() == zoneId)
            {
                return subZones.get(i);
            }
        }

        return null;
    }

    public void addZone(SafeCitySubZone newZone)
    {
        for (int i = 0; i < subZones.size(); i++)
        {
            SafeCitySubZone subZone = subZones.get(i);
            if (subZone.equals(newZone)) { return; }
        }

        subZones.add(newZone);
    }

    public void removeSubZone(SafeCitySubZone subZone)
    {
        subZones.remove(subZone);
    }

    public boolean containsSubZone(SafeCitySubZone newZone)
    {
        for (SafeCitySubZone subZone : subZones)
        {
            if (subZone.equals(newZone))
            {
                return true;
            }
        }

        return false;
    }

    public SafeCitySubZone getExactSubZone(Location location)
    {
        World world = location.getWorld();
        ThinLocation loc = new ThinLocation(location.getBlockX(), location.getBlockY(), location.getBlockZ());

        for (int i = 0; i < subZones.size(); i++)
        {
            SafeCitySubZone subZone = subZones.get(i);

            if (subZone.isInsideZone(loc, world, true))
            {
                return subZone;
            }
        }

        return null;
    }

    public SafeCitySubZone getExactSubZone(ThinLocation location, World world)
    {
        return getExactSubZone(new Location(world, location.getBlockX(), location.getBlockY(), location.getBlockZ()));
    }


}
