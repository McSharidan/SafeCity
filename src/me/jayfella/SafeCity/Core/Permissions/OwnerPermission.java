package me.jayfella.SafeCity.Core.Permissions;

import me.jayfella.SafeCity.Core.ZonePermissionInterface;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public final class OwnerPermission implements ZonePermissionInterface
{
    @Override public final String getPermissionName() { return "Owner"; }
    @Override public boolean requiresOwnerPermission() { return true; }
    @Override public boolean primaryZoneOnly() { return true; }
    @Override public boolean canSetPublic() { return true; }

    @Override public boolean canAccessAllBlocks() { return true; }
    @Override public Material[] getAccessibleBlocks()
    {
        return new Material[0];
    }

    @Override public boolean canPlaceAllBlocks() { return true; }
    @Override public Material[] getPlaceableBlocks()
    {
        return new Material[0];
    }

    @Override public boolean canBreakAllBlocks() { return true; }
    @Override public Material[] getBreakableBlocks()
    {
        return new Material[0];
    }

    @Override public boolean canKillAllEntities() { return true; }
    @Override public EntityType[] getKillableEntities()
    {
        return new EntityType[0];
    }

    @Override public boolean canInteractAllEntities() { return true; }
    @Override public EntityType[] getInteractableEntities()
    {
        return new EntityType[0];
    }

    @Override public boolean canDamageVehicles() { return true; }
    @Override public boolean canRecruit() { return true; }
    @Override public boolean canCreateSubZones() { return true; }
    @Override public boolean canResizeSubZones() { return true; }
    @Override public boolean canDeleteSubZones() { return true; }
    @Override public boolean canRenameSubZones() { return true; }
    @Override public boolean canSellZones() { return true; }
    @Override public boolean canRentZones() { return true; }
    @Override public boolean canDemolish() { return true; }
    @Override public boolean canFill() { return true; }
    @Override public boolean canPlaceLiquid() { return true; }
    @Override public boolean canRetrieveLiquid() { return true; }

	@Override
	public String toString()
    {
        return getPermissionName();
    }
}
