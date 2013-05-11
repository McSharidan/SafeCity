package me.jayfella.SafeCity.Core.Permissions;

import me.jayfella.SafeCity.Core.ZonePermissionInterface;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public final class LandlordPermission implements ZonePermissionInterface
{
    @Override public final String getPermissionName() { return "Landlord"; }
    @Override public boolean requiresOwnerPermission() { return false; }
    @Override public boolean primaryZoneOnly() { return true; }
    @Override public boolean canSetPublic() { return false; }

    @Override public boolean canAccessAllBlocks()	{ return false; }
    @Override public Material[] getAccessibleBlocks()
	{
		return new Material[0];
	}

    @Override public boolean canPlaceAllBlocks() { return false; }
    @Override public Material[] getPlaceableBlocks()
	{
		return new Material[0];
	}

    @Override public boolean canBreakAllBlocks() { return false; }
    @Override public Material[] getBreakableBlocks()
	{
		return new Material[0];
	}

    @Override public boolean canKillAllEntities()	{ return false; }
    @Override public EntityType[] getKillableEntities()
	{
		return new EntityType[0];
	}

    @Override public boolean canInteractAllEntities() { return false; }
    @Override public EntityType[] getInteractableEntities()
	{
		return new EntityType[0];
	}

    @Override public boolean canDamageVehicles() { return false; }
    @Override public boolean canRecruit() {	return false; }
    @Override public boolean canCreateSubZones() { return false; }
    @Override public boolean canResizeSubZones() { return false; }
    @Override public boolean canDeleteSubZones() { return false; }
    @Override public boolean canRenameSubZones() { return false; }
    @Override public boolean canSellZones() { return true; }
    @Override public boolean canRentZones() { return true; }
    @Override public boolean canDemolish() { return false; }
    @Override public boolean canFill() { return false; }
    @Override public boolean canPlaceLiquid() { return false; }
    @Override public boolean canRetrieveLiquid() { return false; }

	@Override
	public String toString()
	{
		return getPermissionName();
	}
}
