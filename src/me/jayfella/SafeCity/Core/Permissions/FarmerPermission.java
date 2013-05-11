package me.jayfella.SafeCity.Core.Permissions;

import me.jayfella.SafeCity.Core.ZonePermissionInterface;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public final class FarmerPermission implements ZonePermissionInterface
{
    @Override public final String getPermissionName() { return "Farmer"; }
    @Override public boolean requiresOwnerPermission() { return false; }
    @Override public boolean primaryZoneOnly() { return false; }
    @Override public boolean canSetPublic() { return true; }

    @Override public boolean canAccessAllBlocks() { return false; }
    @Override public Material[] getAccessibleBlocks()
    {
        return new Material[]
        {
            Material.CHEST,
            Material.FENCE_GATE,
            Material.POWERED_MINECART,
            Material.WORKBENCH,
        };
    }

    @Override public boolean canPlaceAllBlocks() { return false; }
    @Override public Material[] getPlaceableBlocks()
	{
            return new Material[]
            {
                Material.BAKED_POTATO,
                Material.CACTUS,
                Material.CARROT,
                Material.CROPS,
                Material.DIRT,
                Material.MELON_BLOCK,
                Material.MELON_SEEDS,
                Material.MELON_STEM,
                Material.NETHER_WARTS,
                Material.POTATO,
                Material.RED_MUSHROOM,
                Material.SAND,
                Material.SOUL_SAND,
                Material.SOIL,
                Material.SUGAR_CANE,
                Material.SUGAR_CANE_BLOCK,
                Material.CROPS,
                Material.PUMPKIN,
                Material.PUMPKIN_STEM,
                Material.SAPLING,
                Material.LONG_GRASS,
            };
	}

    @Override public boolean canBreakAllBlocks() { return false; }
    @Override public Material[] getBreakableBlocks()
	{
            return new Material[]
            {
                Material.BAKED_POTATO,
                Material.CACTUS,
                Material.CARROT,
                Material.CROPS,
                Material.DIRT,
                Material.MELON_BLOCK,
                Material.MELON_SEEDS,
                Material.MELON_STEM,
                Material.NETHER_WARTS,
                Material.POTATO,
                Material.RED_MUSHROOM,
                Material.SAND,
                Material.SOUL_SAND,
                Material.SOIL,
                Material.SUGAR_CANE,
                Material.SUGAR_CANE_BLOCK,
                Material.CROPS,
                Material.PUMPKIN,
                Material.PUMPKIN_STEM,
                Material.LEAVES,
                Material.LOG,
                Material.SAPLING,
                Material.GRASS,
                Material.LONG_GRASS,
            };
	}

    @Override public boolean canKillAllEntities() { return false; }
    @Override public EntityType[] getKillableEntities()
	{
            return new EntityType[]
            {
                EntityType.CHICKEN,
                EntityType.COW,
                EntityType.MUSHROOM_COW,
                EntityType.PIG,
                EntityType.SHEEP,
                EntityType.SQUID,
                EntityType.MINECART,
            };
	}

    @Override public boolean canInteractAllEntities() { return false; }
    @Override public EntityType[] getInteractableEntities()
	{
            return new EntityType[]
            {
                EntityType.COW,
                EntityType.SHEEP,
                EntityType.MINECART,
                EntityType.MUSHROOM_COW,
            };
	}

    @Override public boolean canDamageVehicles() { return false; }
    @Override public boolean canRecruit() { return false; }
    @Override public boolean canCreateSubZones() { return true; }
    @Override public boolean canResizeSubZones() { return true; }
    @Override public boolean canDeleteSubZones() { return true; }
    @Override public boolean canRenameSubZones() { return true; }
    @Override public boolean canSellZones() { return false; }
    @Override public boolean canRentZones() { return false; }
    @Override public boolean canDemolish() { return false; }
    @Override public boolean canFill() { return false; }
    @Override public boolean canPlaceLiquid() { return true; }
    @Override public boolean canRetrieveLiquid() { return true; }

    @Override
    public String toString()
    {
        return getPermissionName();
    }

}