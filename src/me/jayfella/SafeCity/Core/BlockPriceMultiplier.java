package me.jayfella.SafeCity.Core;

public enum BlockPriceMultiplier
{
    Level_1(0, 1.0, "1.0x"),
    Level_2(2500, 1.5, "1.5x"),
    Level_3(10000, 2.0, "2.0x"),
    Level_4(25000, 2.5, "2.5x"),
    Level_5(50000, 3.0, "3.0x"),
    Level_6(100000, 4.0, "4.0x"),
    Level_7(150000, 5.0, "5.0x"),
    Level_8(175000, 7.5, "7.5x");

    private final int minimumBlocks;
    private final double multiplier;
    private final String friendlyName;

    private BlockPriceMultiplier(int minimumBlocks, double multiplier, String friendlyName)
    {
        this.minimumBlocks = minimumBlocks;
        this.multiplier = multiplier;
        this.friendlyName = friendlyName;
    }

    @Override
    public String toString()
    {
        return this.friendlyName;
    }

    public int getMinimumBlocks()
    {
        return this.minimumBlocks;
    }

    public double getMultiplier()
    {
        return this.multiplier;
    }

    public String getFriendlyName()
    {
        return this.friendlyName;
    }


}
