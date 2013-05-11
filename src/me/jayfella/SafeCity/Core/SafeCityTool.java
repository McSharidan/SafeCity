package me.jayfella.SafeCity.Core;

import org.bukkit.Material;

public enum SafeCityTool
{
	ZoneTool(Material.WOOD_SPADE, "Wood Spade"),
	ZoneTool3d(Material.GOLD_SPADE, "Gold Spade"),
	ZoneInfoTool(Material.STICK, "Stick");

    private Material material;
    private String friendlyName;

    private SafeCityTool(Material material, String friendlyName)
    {
        this.material = material;
        this.friendlyName = friendlyName;
    }

    public Material material() { return this.material; }
    public String friendlyName() { return this.friendlyName; }
}