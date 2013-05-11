package me.jayfella.SafeCity.Core;

public final class ThinLocation
{
    private final int x, y, z;

    public ThinLocation(final int x, final int y, final int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getBlockX() { return this.x; }
    public int getBlockY() { return this.y; }
    public int getBlockZ() { return this.z; }
}
