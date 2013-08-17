package me.jayfella.SafeCity.Core;

import java.util.Objects;

public final class ChunkLocation
{
    private final String world;
    private final int x, z;

    public ChunkLocation(String world, int x, int z)
    {
        this.world = world;
        this.x = x;
        this.z = z;
    }

    public int getChunkX() { return this.x; }
    public int getChunkZ() { return this.z; }

    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof ChunkLocation)) { return false; }

        ChunkLocation otherChunkLoc = (ChunkLocation)other;

        boolean worldIsSame = this.world.equals(otherChunkLoc.world);
        boolean xIsSame = (this.x == otherChunkLoc.x);
        boolean zIsSame = (this.z == otherChunkLoc.z);

        return (worldIsSame && xIsSame && zIsSame);
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 73 * hash + Objects.hashCode(this.world);
        hash = 73 * hash + this.x;
        hash = 73 * hash + this.z;
        return hash;
    }


}
