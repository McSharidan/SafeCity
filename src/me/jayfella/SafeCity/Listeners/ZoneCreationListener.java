package me.jayfella.SafeCity.Listeners;

import me.jayfella.SafeCity.Core.PluginPermission;
import me.jayfella.SafeCity.Core.SafeCityTool;
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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public final class ZoneCreationListener implements Listener
{
	private final SafeCityContext context;

	public ZoneCreationListener (SafeCityContext context)
	{
		this.context = context;
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) { return; }
		if (event.getClickedBlock() == null || event.getClickedBlock().getType() == Material.AIR) { return; }

		SafeCityPlayer scPlayer = context.getPlayer(event.getPlayer());

        if (scPlayer.getCoolDownState()) { return; }
		if (scPlayer.getZoneManager().isResizing()) { return; }

		boolean hasTool = false;
		if (scPlayer.getBukkitPlayer().getItemInHand().getType() == SafeCityTool.ZoneTool.material()) { hasTool = true; }
		if (scPlayer.getBukkitPlayer().getItemInHand().getType() == SafeCityTool.ZoneTool3d.material()) { hasTool = true; }
		if (!hasTool) { return; }

        World blockWorld = event.getClickedBlock().getWorld();
        Location blockLocation = event.getClickedBlock().getLocation();
        ThinLocation blockThinLocation = context.toThinLocation(blockLocation);

		// does a zone already exist here?
		SafeCityZone zone = context.getZone(blockThinLocation, blockWorld);
		SafeCitySubZone subZone = context.getSubZone(blockThinLocation, blockWorld);

		// if clicked a corner, ignore (resizing)
		if (zone != null && zone.isCornerLocation(blockThinLocation, blockWorld, false)) { return; }
		if (subZone != null && subZone.isCornerLocation(blockThinLocation, blockWorld, true)) { return; }

        // check if player has permission
        if (!this.checkPermission(event, scPlayer, zone, subZone)) { return; }

        initCreation(event, scPlayer, zone);

	}

	private boolean checkPermission(PlayerInteractEvent event, SafeCityPlayer scPlayer, SafeCityZone zone, SafeCitySubZone subZone)
	{
		// cannot create sub-zones within sub-zones.
		if (subZone != null)
		{
			scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Too_Many_Children());

			scPlayer.getZoneManager().clearCreationData();
			scPlayer.startCoolDown(2L);

			event.setCancelled(true);
			return false;
		}

        // is using the 3D tool outside a main zone.
		if (zone == null && scPlayer.getBukkitPlayer().getItemInHand().getType() == SafeCityTool.ZoneTool3d.material())
		{
			scPlayer.getZoneManager().clearCreationData();
			scPlayer.startCoolDown(2L);

			scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Create_3DZone_InPrimary_Only());
			return false;
		}

		// check permission
		if (zone != null)
		{
			for (ZonePermissionType p : ZonePermissionType.values())
			{
				// if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), p))
                if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), p))
				{
					if (p.getPermissions().canCreateSubZones())
					{
						return true;
					}
				}
			}
            
            if (context.getBukkitPermissions().has((World)null, scPlayer.getBukkitPlayer().getName(), PluginPermission.Staff_Override.permissionNode()))
            {
                scPlayer.getBukkitPlayer().sendMessage(ChatColor.RED + ">>** Staff Override");
                return true;
            }

			scPlayer.getZoneManager().clearCreationData();
            scPlayer.startCoolDown(2L);

            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().No_Permission());
            
            return false;
		}

		return true;
	}

	private void initCreation(PlayerInteractEvent event, SafeCityPlayer scPlayer, SafeCityZone zone)
	{
        if (zone == null & scPlayer.getZoneManager().getSubZoneCreationParent() == null)
        {
            scPlayer.getZoneManager().setIsCreatingZone(true);
            scPlayer.getZoneManager().setNewZoneLocation1(context.toThinLocation(event.getClickedBlock().getLocation()));

            // createPrimaryZone(event, scPlayer);
        }
		else
        {
            // first click of sub-zone creation
            if (!scPlayer.getZoneManager().isCreatingSubZone())
            {
                scPlayer.getZoneManager().setIsCreatingSubZone(true);
                scPlayer.getZoneManager().setSubZoneCreationParent(zone);

                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Sub_Creating());

                scPlayer.getZoneManager().setNewZoneLocation1(context.toThinLocation(event.getClickedBlock().getLocation()));

                return;
            }
            else // second click
            {
                createSubZone(scPlayer, event, zone);
            }
        }

        scPlayer.getZoneManager().clearCreationData();
		scPlayer.startCoolDown(2L);
	}

    private void createSubZone(SafeCityPlayer scPlayer, PlayerInteractEvent event, SafeCityZone zone)
    {
        // define second click
		scPlayer.getZoneManager().setNewZoneLocation2(context.toThinLocation(event.getClickedBlock().getLocation()));

        // subzone-creation zone click mismatch
        SafeCityZone firstClickZone = context.getZone(scPlayer.getZoneManager().getNewZoneLocation1(), scPlayer.getBukkitPlayer().getWorld());
        SafeCityZone secondClickZone = context.getZone(scPlayer.getZoneManager().getNewZoneLocation2(), scPlayer.getBukkitPlayer().getWorld());

        if (firstClickZone != secondClickZone)
		{
			scPlayer.getZoneManager().clearCreationData();
			scPlayer.startCoolDown(2L);

			scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Mismatch());
			return;
		}
        
        ThinLocation[] clickedCorners = context.sortCorners(scPlayer.getZoneManager().getNewZoneLocation1(), scPlayer.getZoneManager().getNewZoneLocation2());

        // check if subzone is on the edge of the primary zone (lesser)
        if (clickedCorners[0].getBlockX() == zone.getLesserCorner().getBlockX() || clickedCorners[0].getBlockZ() == zone.getLesserCorner().getBlockZ())
        {
            scPlayer.getBukkitPlayer().sendMessage(ChatColor.RED + "Sub-zone cannot be on the edge of the primary zone.");
            return;
        }
        
        // check if subzone is on the edge of the primary zone (greater)
        if (clickedCorners[1].getBlockX() == zone.getGreaterCorner().getBlockX() || clickedCorners[1].getBlockZ() == zone.getGreaterCorner().getBlockZ())
        {
            scPlayer.getBukkitPlayer().sendMessage(ChatColor.RED + "Sub-zone cannot be on the edge of the primary zone.");
            return;
        }
        
        
        // 2D SubZone
        if (scPlayer.getBukkitPlayer().getItemInHand().getType() == SafeCityTool.ZoneTool.material())
        {
            clickedCorners[0] = new ThinLocation(clickedCorners[0].getBlockX(), 1, clickedCorners[0].getBlockZ());
            clickedCorners[1] = new ThinLocation(clickedCorners[1].getBlockX(), (scPlayer.getBukkitPlayer().getWorld().getMaxHeight() -1), clickedCorners[1].getBlockZ());
        }

        int width = Math.abs(clickedCorners[1].getBlockX() - clickedCorners[0].getBlockX()) + 1;
		int length = Math.abs(clickedCorners[1].getBlockZ() - clickedCorners[0].getBlockZ()) + 1;
		int height = Math.abs(clickedCorners[1].getBlockY() - clickedCorners[0].getBlockY()) + 1;

        if (scPlayer.getBukkitPlayer().getItemInHand().getType() == SafeCityTool.ZoneTool.material())
		{
			if (width < 5 || length < 3)
			{
				scPlayer.getZoneManager().clearCreationData();
				scPlayer.startCoolDown(2L);

				scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().SubZone_Too_Small());
				return;
			}
		}

        if (isOverlapping(scPlayer, clickedCorners[0], clickedCorners[1]))
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Overlap());
            return;
        }

		if (scPlayer.getBukkitPlayer().getItemInHand().getType() == SafeCityTool.ZoneTool3d.material())
		{
			if (width < 5 || length < 5 || height < 3)
			{
				scPlayer.getZoneManager().clearCreationData();
				scPlayer.startCoolDown(2L);

				scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().SubZone3D_Too_Small());
				return;
			}
		}

        if (scPlayer.getBukkitPlayer().getItemInHand().getType() != SafeCityTool.ZoneTool3d.material())
		{
            clickedCorners[0] = new ThinLocation(clickedCorners[0].getBlockX(), 1, clickedCorners[0].getBlockZ());
            clickedCorners[1] = new ThinLocation(clickedCorners[1].getBlockX(), (scPlayer.getBukkitPlayer().getWorld().getMaxHeight() -1), clickedCorners[1].getBlockZ());
		}

         
        
        SafeCitySubZone newSubZone = new SafeCitySubZone(context, zone.getId(), zone.getFounder(), scPlayer.getBukkitPlayer().getWorld(), clickedCorners[0], clickedCorners[1]);

        context.addSubZone(newSubZone);

        scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Sub_Created());

			// update the children
			scPlayer.getZoneManager().setCurrentZone(zone);

			context.getConsole().sendMessage(new StringBuilder()
				.append(ChatColor.GOLD)
				.append("Player ")
				.append(ChatColor.RED)
				.append(scPlayer.getBukkitPlayer().getName())
				.append(ChatColor.GOLD)
				.append(" created a new sub-zone. W:")
				.append(ChatColor.RED)
				.append(newSubZone.getWidth())
				.append(ChatColor.GOLD)
				.append(" L:")
				.append(ChatColor.RED)
				.append(newSubZone.getLength())
				.append(ChatColor.GOLD)
				.append(" H:")
				.append(ChatColor.RED)
				.append(newSubZone.getHeight()).toString());

            context.addNewSubZoneToMap(newSubZone);
    }

    private boolean isOverlapping(SafeCityPlayer scPlayer, ThinLocation lesserCorner, ThinLocation greaterCorner)
    {
        World world = scPlayer.getBukkitPlayer().getWorld();

        if (scPlayer.getZoneManager().isCreatingZone())
        {
            for (SafeCityZone zone : context.getZones())
            {
                if (context.isOverlap(lesserCorner, greaterCorner, zone.getLesserCorner(), zone.getGreaterCorner()))
                {
                    return true;
                }
            }

            return false;
        }
        else if (scPlayer.getZoneManager().isCreatingSubZone())
        {
            SafeCityZone parentZone = context.getZone(scPlayer.getZoneManager().getNewZoneLocation2(), world);

            for (SafeCitySubZone subZone : parentZone.getChildren())
            {
                if (context.isOverlap(lesserCorner, greaterCorner, subZone.getLesserCorner(), subZone.getGreaterCorner()))
                {
                    return true;
                }
            }
        }

        return false;
    }



}
