package me.jayfella.SafeCity.Core.Permissions;

import me.jayfella.SafeCity.Core.ZonePermissionInterface;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public final class RenterPermission implements ZonePermissionInterface
{
    @Override public final String getPermissionName() { return "Renter"; }
    @Override public boolean requiresOwnerPermission() { return false; }
    @Override public boolean primaryZoneOnly() { return false; }
    @Override public boolean canSetPublic() { return true; }

    @Override public boolean canAccessAllBlocks()	{ return false; }
    @Override public Material[] getAccessibleBlocks()
	{
		return new Material[]
			{
				Material.ANVIL,
				Material.BED,
				Material.BOAT,
				Material.BREWING_STAND,
				Material.CAULDRON,
				Material.CHEST,
				Material.DISPENSER,
				Material.ENDER_CHEST,
				Material.ENCHANTMENT_TABLE,
				Material.FURNACE,
				Material.MINECART,
				Material.TRAP_DOOR,
				Material.WOODEN_DOOR,
				Material.WORKBENCH,
				Material.WOOD_BUTTON,
				Material.STONE_BUTTON,
				Material.STONE_PLATE,
				Material.WOOD_PLATE,
				Material.LEVER,
                Material.JUKEBOX,
                Material.NOTE_BLOCK,
			};
	}

    @Override public boolean canPlaceAllBlocks() { return false; }
    @Override public Material[] getPlaceableBlocks()
	{
		return new Material[]
				{
					Material.ANVIL,
					Material.BED,
					Material.BED_BLOCK,
					Material.BOAT,
					Material.BREWING_STAND,
					Material.CAULDRON,
					Material.CHEST,
					Material.DISPENSER,
					Material.ENDER_CHEST,
					Material.ENCHANTMENT_TABLE,
					Material.FURNACE,
					Material.MINECART,
					Material.WORKBENCH,
					Material.WALL_SIGN,
					Material.SIGN_POST,
				};
	}

    @Override public boolean canBreakAllBlocks() { return false; }
    @Override public Material[] getBreakableBlocks()
	{
		return new Material[]
				{
					Material.ANVIL,
					Material.BED,
                    Material.BED_BLOCK,
					Material.BOAT,
					Material.BREWING_STAND,
					Material.CAULDRON,
					Material.CHEST,
					Material.DISPENSER,
					Material.ENDER_CHEST,
					Material.ENCHANTMENT_TABLE,
					Material.FURNACE,
					Material.WORKBENCH,
					Material.WALL_SIGN,
					Material.SIGN_POST,
				};
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
    @Override public boolean canRecruit() { return false; }
    @Override public boolean canCreateSubZones() { return false; }
    @Override public boolean canResizeSubZones() { return false; }
    @Override public boolean canDeleteSubZones() { return false; }
    @Override public boolean canRenameSubZones() { return false; }
    @Override public boolean canSellZones() { return false; }
    @Override public boolean canRentZones() { return false; }
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