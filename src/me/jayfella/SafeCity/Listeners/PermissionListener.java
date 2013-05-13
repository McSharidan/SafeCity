package me.jayfella.SafeCity.Listeners;

import me.jayfella.SafeCity.Core.HostileEntity;
import me.jayfella.SafeCity.Core.ThinLocation;
import me.jayfella.SafeCity.Core.ZonePermissionType;
import me.jayfella.SafeCity.SafeCityContext;
import me.jayfella.SafeCity.SafeCityPlayer;
import me.jayfella.SafeCity.SafeCitySubZone;
import me.jayfella.SafeCity.SafeCityZone;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Fish;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.vehicle.VehicleDamageEvent;

public final class PermissionListener implements Listener
{

	private final SafeCityContext context;

	public PermissionListener(SafeCityContext context)
	{
		this.context = context;
	}

    @EventHandler 
    public void onHopperPlace(BlockPlaceEvent event)
    {
        if (event.getBlock().getType() == Material.HOPPER | event.getBlock().getType() == Material.DROPPER)
        {
            
            Location placedLoc = event.getBlock().getLocation();
            World placedWorld = event.getBlock().getWorld();
            
            // all possible locations...
            
            Location[] allDirections =
            {
                new Location(placedLoc.getWorld(), placedLoc.getBlockX(), placedLoc.getBlockY() + 1, placedLoc.getBlockZ()),
                new Location(placedLoc.getWorld(), placedLoc.getBlockX(), placedLoc.getBlockY() - 1, placedLoc.getBlockZ()),
                
                new Location(placedLoc.getWorld(), placedLoc.getBlockX() - 1, placedLoc.getBlockY(), placedLoc.getBlockZ()),
                new Location(placedLoc.getWorld(), placedLoc.getBlockX() + 1, placedLoc.getBlockY(), placedLoc.getBlockZ()),
                
                new Location(placedLoc.getWorld(), placedLoc.getBlockX(), placedLoc.getBlockY(), placedLoc.getBlockZ() + 1),
                new Location(placedLoc.getWorld(), placedLoc.getBlockX(), placedLoc.getBlockY(), placedLoc.getBlockZ() - 1),
            };
            
            SafeCityZone[] directionalZones =
            {
                context.getZone(context.toThinLocation(allDirections[0]), placedWorld),
                context.getZone(context.toThinLocation(allDirections[1]), placedWorld),
                
                context.getZone(context.toThinLocation(allDirections[2]), placedWorld),
                context.getZone(context.toThinLocation(allDirections[3]), placedWorld),
                
                context.getZone(context.toThinLocation(allDirections[4]), placedWorld),
                context.getZone(context.toThinLocation(allDirections[5]), placedWorld),
            };
            
            SafeCitySubZone[] directionalSubZones =
            {
                context.getSubZone(context.toThinLocation(allDirections[0]), placedWorld),
                context.getSubZone(context.toThinLocation(allDirections[1]), placedWorld),
                
                context.getSubZone(context.toThinLocation(allDirections[2]), placedWorld),
                context.getSubZone(context.toThinLocation(allDirections[3]), placedWorld),
                
                context.getSubZone(context.toThinLocation(allDirections[4]), placedWorld),
                context.getSubZone(context.toThinLocation(allDirections[5]), placedWorld),
            };

            SafeCityZone placedZone = context.getZone(context.toThinLocation(placedLoc), placedWorld);
            SafeCitySubZone placedSubZone = context.getSubZone(context.toThinLocation(placedLoc), placedWorld);
            
            boolean mismatch = false;
            
            for (SafeCityZone z : directionalZones)
            {
                if (z != placedZone)
                {
                    mismatch = true;
                    break;
                }
            }
            
            if (!mismatch)
            {
                for (SafeCitySubZone z : directionalSubZones)
                {
                    if (z != placedSubZone)
                    {
                        mismatch = true;
                    }
                }
            }
            
            if (mismatch)
            {
                Player player = event.getPlayer();
                player.sendMessage(ChatColor.RED + "For security reasons, hoppers or droppers cannot be placed on zone edges.");
                event.setCancelled(true);
            }
            
        }
    }
    
	@EventHandler(ignoreCancelled=true)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (event.getAction() == Action.LEFT_CLICK_AIR) { return; }
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) { return; }

        SafeCityZone zone = context.getZone(context.toThinLocation(event.getClickedBlock().getLocation()), event.getClickedBlock().getWorld());
		if (zone == null) { return; }

		Material clickedItem = event.getClickedBlock().getType();

		boolean isAccessBlock = (
				clickedItem == Material.ANVIL
				|| clickedItem == Material.BED
				|| clickedItem == Material.BOAT
				|| clickedItem == Material.BREWING_STAND
                || clickedItem == Material.BURNING_FURNACE
				|| clickedItem == Material.CAULDRON
				|| clickedItem == Material.CHEST
				|| clickedItem == Material.DISPENSER
				|| clickedItem == Material.ENDER_CHEST
				|| clickedItem == Material.ENCHANTMENT_TABLE
                || clickedItem == Material.FENCE_GATE
				|| clickedItem == Material.FURNACE
				|| clickedItem == Material.MINECART
				|| clickedItem == Material.POWERED_MINECART
				|| clickedItem == Material.STORAGE_MINECART
				|| clickedItem == Material.TRAP_DOOR
				|| clickedItem == Material.WOODEN_DOOR
				|| clickedItem == Material.WORKBENCH
				|| clickedItem == Material.WOOD_BUTTON
				|| clickedItem == Material.STONE_BUTTON
                || clickedItem == Material.TRAPPED_CHEST
				|| clickedItem == Material.LEVER
				|| clickedItem == Material.STONE_PLATE
				|| clickedItem == Material.WOOD_PLATE
                || clickedItem == Material.CAKE
                || clickedItem == Material.CAKE_BLOCK
                || clickedItem == Material.JUKEBOX
                || clickedItem == Material.HOPPER
                || clickedItem == Material.DROPPER
				);

		if (isAccessBlock)
		{
			if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.PHYSICAL))
			{

				SafeCitySubZone subZone = context.getSubZone(context.toThinLocation(event.getClickedBlock().getLocation()), event.getClickedBlock().getWorld());
				SafeCityPlayer scPlayer = context.getPlayer(event.getPlayer());

				Material clickedBlock = event.getClickedBlock().getType();

    			for (ZonePermissionType p : ZonePermissionType.values())
				{
					if (subZone == null)
					{
                        if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), p))
						{
							if (p.getPermissions().canAccessAllBlocks())
							{
								return;
							}
							else
							{
								for (Material m : p.getPermissions().getAccessibleBlocks())
								{
									if (clickedBlock == m)
									{
										return;
									}
								}
							}
						}
					}
					else
					{
                        // if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Owner))
                        if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Owner))
                        {
                            return;
                        }

						if (subZone.hasPermission(scPlayer.getBukkitPlayer().getName(), p) || subZone.hasPermission("*", p))
						{
							if (p.getPermissions().canAccessAllBlocks())
							{
								return;
							}
							else
							{
								for (Material m : p.getPermissions().getAccessibleBlocks())
								{

									if (clickedBlock == m)
									{
										return;
									}
								}
							}
						}
					}
				}
			}

            event.setCancelled(true);
		}


	}

	@EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event)
	{
		SafeCityZone zone = context.getZone(context.toThinLocation(event.getRightClicked().getLocation()), event.getRightClicked().getWorld());
		if (zone == null) { return; }

		SafeCitySubZone subZone = context.getSubZone(context.toThinLocation(event.getRightClicked().getLocation()), event.getRightClicked().getWorld());
		SafeCityPlayer scPlayer = context.getPlayer(event.getPlayer());
		EntityType clickedEntity = event.getRightClicked().getType();

		for (ZonePermissionType p : ZonePermissionType.values())
		{
			if (subZone == null)
			{
				// if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), p) || zone.hasPermission("*", p))
                if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), p))
				{
					if (p.getPermissions().canInteractAllEntities())
					{
						return;
					}
					else
					{
						for (EntityType e : p.getPermissions().getInteractableEntities())
						{
							if (e == clickedEntity)
							{
								return;
							}
						}
					}
				}
			}
			else
			{
                // if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Owner))
                if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Owner))
                {
                    return;
                }

				if (subZone.hasPermission(scPlayer.getBukkitPlayer().getName(), p) || subZone.hasPermission("*", p))
				{
					if (p.getPermissions().canInteractAllEntities())
					{
						return;
					}
					else
					{
						for (EntityType e : p.getPermissions().getInteractableEntities())
						{
							if (e == clickedEntity)
							{
								return;
							}
						}
					}
				}
			}
		}

        event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled=true)
	public void onPlayerDamage(EntityDamageByEntityEvent event)
	{
        Player attacker = null;
	    Entity damageSource = event.getDamager();

	    if ((damageSource instanceof Player))
	    {
	    	attacker = (Player)damageSource;
	    }
	    else if ((damageSource instanceof Arrow))
	    {
	    	Arrow arrow = (Arrow)damageSource;

	    	if ((arrow.getShooter() instanceof Player))
	    	{
	    		attacker = (Player)arrow.getShooter();
	    	}
	    }
	    else if ((damageSource instanceof ThrownPotion))
	    {
	    	ThrownPotion potion = (ThrownPotion)damageSource;

	    	if ((potion.getShooter() instanceof Player))
	    	{
	    		attacker = (Player)potion.getShooter();
	    	}
	    }
        else if ((damageSource instanceof Egg))
        {
            Egg egg = (Egg)damageSource;

            if (egg.getShooter() instanceof Player)
            {
                attacker = (Player)egg.getShooter();
            }
        }
        else if ((damageSource instanceof Fireball))
        {
            Fireball fireball = (Fireball)damageSource;

            if (fireball.getShooter() instanceof Player)
            {
                attacker = (Player)fireball.getShooter();
            }
        }
        else if ((damageSource instanceof Fish))
        {
            Fish fish = (Fish)damageSource;

            if (fish.getShooter() instanceof Player)
            {
                attacker = (Player)fish.getShooter();
            }
        }

	    if (attacker == null || (!(attacker instanceof Player))) { return; }

        SafeCityZone zone = context.getZone(context.toThinLocation(damageSource.getLocation()), damageSource.getWorld());
		if (zone == null) { return; }

		SafeCitySubZone subZone = context.getSubZone(context.toThinLocation(damageSource.getLocation()), damageSource.getWorld());

        if (event.getEntityType() == null) { return; }
        EntityType damagedEntity = event.getEntityType();

        for (HostileEntity hostile : HostileEntity.values())
        {
            if (hostile.getType() == damagedEntity) { return; }
        }

        SafeCityPlayer scPlayer = context.getPlayer(attacker);

		for (ZonePermissionType p : ZonePermissionType.values())
		{
			if (subZone == null)
			{
				// if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), p))
                if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), p))
				{
					if (p.getPermissions().canKillAllEntities())
					{
                        return;
					}
					else
					{
						for (EntityType e : p.getPermissions().getKillableEntities())
						{
							if (event.getEntity().getType() == e)
							{
								return;
							}
						}
					}
				}
			}
			else
			{
                if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Owner))
                {
                    return;
                }
                
				if (subZone.hasPermission(scPlayer.getBukkitPlayer().getName(), p))
				{
					if (p.getPermissions().canKillAllEntities())
					{
						return;
					}
					else
					{
						for (EntityType e : p.getPermissions().getKillableEntities())
						{
							if (event.getEntity().getType() == e)
							{
								return;
							}
						}
					}
				}
			}
		}

        event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled=true)
	public void onVehicleDamage(VehicleDamageEvent event)
	{
		Player attacker = null;
	    Entity damageSource = event.getAttacker();

	    if ((damageSource instanceof Player))
	    {
	    	attacker = (Player)damageSource;
	    }
	    else if ((damageSource instanceof Arrow))
	    {
	    	Arrow arrow = (Arrow)damageSource;

	    	if ((arrow.getShooter() instanceof Player))
	    	{
	    		attacker = (Player)arrow.getShooter();
	    	}
	    }
	    else if ((damageSource instanceof ThrownPotion))
	    {
	    	ThrownPotion potion = (ThrownPotion)damageSource;

	    	if ((potion.getShooter() instanceof Player))
	    	{
	    		attacker = (Player)potion.getShooter();
	    	}

	    }

	    if (attacker == null)
        {
            return;
        }

	    if (!(attacker instanceof Player))
        {
            return;
        }

		SafeCityZone zone = context.getZone(context.toThinLocation(event.getVehicle().getLocation()), event.getVehicle().getWorld());

		if (zone == null)
        {
            return;
        }

		SafeCitySubZone subZone = context.getSubZone(context.toThinLocation(event.getVehicle().getLocation()), event.getVehicle().getWorld());
		SafeCityPlayer scPlayer = context.getPlayer(attacker);

		boolean canDamageVehicles = false;

		for (ZonePermissionType p : ZonePermissionType.values())
		{
			if (subZone == null)
			{
				// if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), p))
                if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), p))
				{
					if (p.getPermissions().canDamageVehicles())
                    {
                        canDamageVehicles = true;
                    }
				}
			}
			else
			{
				if (subZone.hasPermission(scPlayer.getBukkitPlayer().getName(), p))
				{
					if (p.getPermissions().canDamageVehicles())
                    {
                        canDamageVehicles = true;
                    }
				}
			}
		}

		if (!canDamageVehicles)
        {
            event.setCancelled(true);
        }
	}

	@EventHandler(ignoreCancelled=true)
	public void onBlockBreak(BlockBreakEvent event)
	{
		if (event.getBlock() == null || event.getBlock().getType() == Material.AIR)
        {
            return;
        }

		SafeCityZone zone = context.getZone(context.toThinLocation(event.getBlock().getLocation()), event.getBlock().getWorld());
		if (zone == null) { return; }

		SafeCitySubZone subZone = context.getSubZone(context.toThinLocation(event.getBlock().getLocation()), event.getBlock().getWorld());
		SafeCityPlayer scPlayer = context.getPlayer(event.getPlayer());

		for (ZonePermissionType p : ZonePermissionType.values())
		{
			if (subZone == null)
			{
				// if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), p))
                if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), p))
				{
					if (p.getPermissions().canBreakAllBlocks())
					{
						return;
					}
					else
					{
						for (Material m : p.getPermissions().getBreakableBlocks())
						{
							if (event.getBlock().getType() == m)
							{
								return;
							}
						}
					}
				}
			}
			else
			{
                // if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Owner))
                if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Owner))
                {
                    return;
                }

				if (subZone.hasPermission(scPlayer.getBukkitPlayer().getName(), p))
				{
					if (p.getPermissions().canBreakAllBlocks())
					{
						return;
					}
					else
					{
						for (Material m : p.getPermissions().getBreakableBlocks())
						{
							if (event.getBlock().getType() == m)
							{
								return;
							}
						}
					}
				}
			}
		}

		 event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled=true)
	public void onBlockPlace(BlockPlaceEvent event)
	{
		SafeCityZone zone = context.getZone(context.toThinLocation(event.getBlock().getLocation()), event.getBlock().getWorld());
		if (zone == null) { return; }

        SafeCityPlayer scPlayer = context.getPlayer(event.getPlayer());
		SafeCitySubZone subZone = context.getSubZone(context.toThinLocation(event.getBlock().getLocation()), event.getBlock().getWorld());

		for (ZonePermissionType p : ZonePermissionType.values())
		{
			if (subZone == null)
			{
				// if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), p))
                if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), p))
				{
					if (p.getPermissions().canPlaceAllBlocks())
					{
						return;
					}
					else
					{
						for (Material m : p.getPermissions().getPlaceableBlocks())
						{
							if (event.getBlock().getType() == m)
							{
								return;
							}
						}
					}
				}
			}
			else
			{
                if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Owner)) { return; }
                // if (subZone.isBuyer(scPlayer.getBukkitPlayer().getName())) { return; }

				if (subZone.hasPermission(scPlayer.getBukkitPlayer().getName(), p))
				{
					if (p.getPermissions().canPlaceAllBlocks())
					{
						return;
					}
					else
					{
						for (Material m : p.getPermissions().getPlaceableBlocks())
						{
							if (event.getBlock().getType() == m)
							{
								return;
							}
						}
					}
				}
			}
		}

        event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled=true)
	public void onBucketEmpty(PlayerBucketEmptyEvent event)
	{
		if (event.getBlockClicked() == null || event.getBlockClicked().getType() == Material.AIR) { return; }

		Location waterLocation = event.getBlockClicked().getLocation();
		waterLocation.setX(waterLocation.getBlockX() + event.getBlockFace().getModX());
		waterLocation.setY(waterLocation.getBlockY() + event.getBlockFace().getModY());
		waterLocation.setZ(waterLocation.getBlockZ() + event.getBlockFace().getModZ());

		SafeCityZone zone = context.getZone(context.toThinLocation(waterLocation), waterLocation.getWorld());
		if (zone == null) { return; }

		SafeCitySubZone subZone = context.getSubZone(context.toThinLocation(waterLocation), waterLocation.getWorld());
		SafeCityPlayer scPlayer = context.getPlayer(event.getPlayer());

		for (ZonePermissionType p : ZonePermissionType.values())
		{
			if (subZone == null)
			{
				// if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), p))
                if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), p))
				{
					if (p.getPermissions().canPlaceLiquid())
                    {
                        return;
                    }
				}
			}
			else
			{
                // if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Owner))
                if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Owner))
                {
                    return;
                }

				if (subZone.hasPermission(scPlayer.getBukkitPlayer().getName(), p))
				{
					if (p.getPermissions().canPlaceLiquid())
                    {
                        return;
                    }
				}
			}
		}

		 event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled=true)
	public void onPlayerBucketFill(PlayerBucketFillEvent event)
	{
		if (event.getBlockClicked() == null || event.getBlockClicked().getType() == Material.AIR) { return; }

		Location waterLocation = event.getBlockClicked().getLocation();

        SafeCityZone zone = context.getZone(context.toThinLocation(waterLocation), waterLocation.getWorld());
		if (zone == null) { return; }

		SafeCitySubZone subZone = context.getSubZone(context.toThinLocation(waterLocation), waterLocation.getWorld());
		SafeCityPlayer scPlayer = context.getPlayer(event.getPlayer());

		for (ZonePermissionType p : ZonePermissionType.values())
		{
			if (subZone == null)
			{
				// if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), p))
                if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), p))
				{
					if (p.getPermissions().canRetrieveLiquid())
                    {
                        return;
                    }
				}
			}
			else
			{
                // if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Owner))
                if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Owner))
                {
                    return;
                }

				if (subZone.hasPermission(scPlayer.getBukkitPlayer().getName(), p))
				{
					if (p.getPermissions().canRetrieveLiquid())
                    {
                        return;
                    }
				}
			}
		}

        event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled=true)
	public void onHanging(HangingPlaceEvent event)
	{
		if (event.getBlock() == null || event.getBlock().getType() == Material.AIR) { return; }

		SafeCityZone zone = context.getZone(context.toThinLocation(event.getBlock().getLocation()), event.getBlock().getWorld());
		if (zone == null) { return; }

		SafeCitySubZone subZone = context.getSubZone(context.toThinLocation(event.getBlock().getLocation()), event.getBlock().getWorld());
		SafeCityPlayer scPlayer = context.getPlayer(event.getPlayer());

		for (ZonePermissionType p : ZonePermissionType.values())
		{
			if (subZone == null)
			{
				// if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), p))
                if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), p))
				{
					if (p.getPermissions().canPlaceAllBlocks())
					{
						return;
					}
					else
					{
						for (Material m : p.getPermissions().getPlaceableBlocks())
						{
							if (event.getBlock().getType() == m)
							{
								return;
							}
						}
					}
				}
			}
			else
			{
                // if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Owner))
                if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Owner))
                {
                    return;
                }

				if (subZone.hasPermission(scPlayer.getBukkitPlayer().getName(), p))
				{
					if (p.getPermissions().canPlaceAllBlocks())
					{
						return;
					}
					else
					{
						for (Material m : p.getPermissions().getPlaceableBlocks())
						{
							if (event.getBlock().getType() == m)
							{
								return;
							}
						}
					}
				}
			}
		}

		event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled=true)
	public void onHangingBreak(HangingBreakEvent event)
	{
		if (!(event instanceof HangingBreakByEntityEvent))
	    {
			event.setCancelled(true);
			return;
	    }

		HangingBreakByEntityEvent entityEvent = (HangingBreakByEntityEvent)event;
	    Entity remover = entityEvent.getRemover();

	    if (!(remover instanceof Player))
	    {
	    	event.setCancelled(true);
	    	return;
	    }

	    SafeCityZone zone = context.getZone(context.toThinLocation(event.getEntity().getLocation()), event.getEntity().getWorld());
		if (zone == null) { return; }

		Player player = (Player)entityEvent.getRemover();
		SafeCitySubZone subZone = context.getSubZone(context.toThinLocation(event.getEntity().getLocation()), event.getEntity().getWorld());
		SafeCityPlayer scPlayer = context.getPlayer(player);

		for (ZonePermissionType p : ZonePermissionType.values())
		{
			if (subZone == null)
			{
				// if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), p))
                if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), p))
				{
					if (p.getPermissions().canBreakAllBlocks())
					{
						return;
					}
					else
					{
						for (Material m : p.getPermissions().getBreakableBlocks())
						{
							if (entityEvent.getEntity() instanceof ItemFrame)
							{
								if (m == Material.ITEM_FRAME)
                                {
                                    return;
                                }
							}

							if (entityEvent.getEntity() instanceof Painting)
							{
								if (m == Material.ITEM_FRAME)
                                {
                                    return;
                                }
							}
						}
					}
				}
			}
			else
			{
                // if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Owner))
                if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Owner))
                {
                    return;
                }

				if (subZone.hasPermission(scPlayer.getBukkitPlayer().getName(), p))
				{
					if (p.getPermissions().canBreakAllBlocks())
					{
						return;
					}
					else
					{
						for (Material m : p.getPermissions().getBreakableBlocks())
						{
							if (entityEvent.getEntity() instanceof ItemFrame)
							{
								if (m == Material.ITEM_FRAME)
                                {
                                    return;
                                }
							}

							if (entityEvent.getEntity() instanceof Painting)
							{
								if (m == Material.ITEM_FRAME)
                                {
                                    return;
                                }
							}
						}
					}
				}
			}
		}

		event.setCancelled(true);
	}

    @EventHandler(ignoreCancelled=true)
    public void onPlayerPortal(PlayerPortalEvent event)
    {
        if (event.getCause() != TeleportCause.NETHER_PORTAL)
        {
            return;
        }

        Player player = event.getPlayer();
        ThinLocation playerLoc = context.toThinLocation(player.getLocation());
        
        SafeCityZone zone = context.getZone(playerLoc, player.getWorld());
        
        if (zone == null) 
        { 
            return;
        }

        boolean isAllowed = false;
        
        if (zone.isResident(player.getName()))
        {
            isAllowed = true;
        }
        
        if (zone.hasPermission(player.getName(), ZonePermissionType.Owner))
        {
            isAllowed = true;
        }
        
        if (!isAllowed)
        {
            event.setCancelled(true);
            player.sendMessage(context.getMessageHandler().Portal_Resident_Only(zone.getName()));
        }
        
        /* if (!(zone.isResident(player.getName()) | zone.hasPermission(player.getName(), ZonePermissionType.Owner)))
        {
            event.setCancelled(true);
            player.sendMessage(context.getMessageHandler().Portal_Resident_Only(zone.getName()));
        }*/
        
    }



}
