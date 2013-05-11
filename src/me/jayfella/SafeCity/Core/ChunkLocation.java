package me.jayfella.SafeCity.Core;

public final class ChunkLocation
{
    private final int x, z;

    public ChunkLocation(int x, int z)
    {
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

        boolean xIsSame = (this.x == otherChunkLoc.x);
        boolean zIsSame = (this.z == otherChunkLoc.z);

        return (xIsSame && zIsSame);
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 41 * hash + this.x;
        hash = 41 * hash + this.z;
        return hash;
    }
}
