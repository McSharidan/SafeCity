package me.jayfella.SafeCity.Core;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public  interface ZonePermissionInterface
{
	String getPermissionName();

	public boolean requiresOwnerPermission();
	public boolean primaryZoneOnly();
	public boolean canSetPublic();

	boolean canAccessAllBlocks();
	Material[] getAccessibleBlocks();

	boolean canPlaceAllBlocks();
	Material[] getPlaceableBlocks();

	boolean canBreakAllBlocks();
	Material[] getBreakableBlocks();

	boolean canKillAllEntities();
	EntityType[] getKillableEntities();

	boolean canInteractAllEntities();
	EntityType[] getInteractableEntities();

	boolean canDamageVehicles();

	boolean canRecruit();

	boolean canCreateSubZones();
	boolean canResizeSubZones();
	boolean canDeleteSubZones();
	boolean canRenameSubZones();

	boolean canSellZones();
	boolean canRentZones();

	boolean canDemolish();
	boolean canFill();

	boolean canPlaceLiquid();
	boolean canRetrieveLiquid();

    @Override
	String toString();
}
