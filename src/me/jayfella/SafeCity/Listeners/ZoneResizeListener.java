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

public final class ZoneResizeListener implements Listener
{
    private final SafeCityContext context;

    public ZoneResizeListener(SafeCityContext context)
    {
        this.context = context;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        if (event.getClickedBlock() == null || event.getClickedBlock().getType() == Material.AIR) { return; }
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) { return; }

        SafeCityPlayer scPlayer = context.getPlayer(event.getPlayer());

        if (scPlayer.getZoneManager().isCreatingZone()) { return; }
        if (scPlayer.getCoolDownState()) { return; }

        boolean hasTool = false;
        if (scPlayer.getBukkitPlayer().getItemInHand().getType() == SafeCityTool.ZoneTool.material()) { hasTool = true; }
        if (scPlayer.getBukkitPlayer().getItemInHand().getType() == SafeCityTool.ZoneTool3d.material()) { hasTool = true; }
        if (!hasTool) { return; }

        World blockWorld = event.getClickedBlock().getWorld();
        Location blockLocation = event.getClickedBlock().getLocation();
        ThinLocation blockThinLocation = context.toThinLocation(blockLocation);

        if (!scPlayer.getZoneManager().isResizing())
        {
            SafeCityZone zone = context.getZone(blockThinLocation, blockWorld);
            if (zone == null) { return; }
            SafeCitySubZone subZone = context.getSubZone(blockThinLocation, blockWorld);

            boolean clickedCorner = false;
            if (zone.isCornerLocation(blockThinLocation, blockWorld, false)) { clickedCorner = true; }
            if (subZone != null && subZone.isCornerLocation(blockThinLocation, blockWorld, true)) { clickedCorner = true; }
            if (!clickedCorner) { return; }

            if (!checkPermission(scPlayer, zone, subZone)) { return; }

            initResizing(event, scPlayer, zone, subZone);
        }
        else
        {
            initResizing(event, scPlayer, null, null);
        }
    }

    private boolean checkPermission(SafeCityPlayer scPlayer, SafeCityZone zone, SafeCitySubZone subZone)
    {
        if (subZone == null)
        {
            if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), ZonePermissionType.Owner))
            {
                return true;
            }
            
            if (context.getBukkitPermissions().has((World)null, scPlayer.getBukkitPlayer().getName(), PluginPermission.Staff_Override.permissionNode()))
            {
                scPlayer.getBukkitPlayer().sendMessage(ChatColor.RED + ">>** Staff Override");
                return true;
            }
            
            return false;
        }
        else
        {
            for (ZonePermissionType p : ZonePermissionType.values())
            {
                if (zone.hasPermission(scPlayer.getBukkitPlayer().getName(), p))
                {
                    if (p.getPermissions().canResizeSubZones())
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

            return false;
        }
    }

    private void initResizing(PlayerInteractEvent event, SafeCityPlayer scPlayer, SafeCityZone zone, SafeCitySubZone subZone)
    {
        // resizing a 3D zone with the 2D tool
        if (subZone != null)
	    {
	    	if (subZone.getLesserCorner().getBlockY() != 1 && subZone.getGreaterCorner().getBlockY() != 255)
	    	{
                    if (scPlayer.getBukkitPlayer().getItemInHand().getType() != SafeCityTool.ZoneTool3d.material())
                    {
                        scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone3dTool_Only());
                        return;
                    }
	    	}
	    }

        // first click
        if (!scPlayer.getZoneManager().isResizing())
        {
            scPlayer.getZoneManager().setIsResizing(true);

            ThinLocation clickedLocation  = context.toThinLocation(event.getClickedBlock().getLocation());
            scPlayer.getZoneManager().setResizeCornerChanged(clickedLocation);

            if (subZone == null)
            {
                scPlayer.getZoneManager().setResizeZone(zone);
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Resizing(zone.getName()));
            }
            else
            {
                scPlayer.getZoneManager().setResizeSubZone(subZone);
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().SubZone_Resizing(subZone.getName()));
            }

            return;
        }
        else // second click
        {
            if (scPlayer.getZoneManager().getResizeZone() != null)
            {
                resizeZone(event, scPlayer);
            }
            else if (scPlayer.getZoneManager().getResizeSubZone() != null)
            {
                resizeSubZone(event, scPlayer);
            }
        }

        scPlayer.getZoneManager().clearResizeData();
        scPlayer.startCoolDown(2L);
    }

    private boolean subZoneOutside(ThinLocation rect1Lesser, ThinLocation rect1Greater, ThinLocation rect2Lesser, ThinLocation rect2Greater)
    {
        boolean isOutside = false;

        if (rect2Lesser.getBlockX() <= rect1Lesser.getBlockX())
        {
            isOutside = true;
        }

        if (rect2Lesser.getBlockZ() <= rect1Lesser.getBlockZ())
        {
            isOutside = true;
        }

        if (rect2Greater.getBlockX() >= rect1Greater.getBlockX())
        {
            isOutside = true;
        }

        if (rect2Greater.getBlockZ() >= rect1Greater.getBlockZ())
        {
            isOutside = true;
        }

        return isOutside;
    }

    private void resizeZone(PlayerInteractEvent event, SafeCityPlayer scPlayer)
    {
        // too close
        /*SafeCityZone nearZone = context.isTownTooClose(scPlayer, event.getClickedBlock().getLocation());
        if (nearZone != null)
        {
            scPlayer.getZoneManager().clearResizeData();
            scPlayer.startCoolDown(2L);

            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Too_Close(nearZone.getName()));

            return;
        }*/

        ThinLocation[] zoneCorners = scPlayer.getZoneManager().getResizeZone().getCorners();
        zoneCorners = context.sortCorners(zoneCorners[0], zoneCorners[1]);

        int oldSize = scPlayer.getZoneManager().getResizeZone().getSurfaceArea();

        ThinLocation firstClick = scPlayer.getZoneManager().getResizeCornerChanged();
		ThinLocation secondClick = context.toThinLocation(event.getClickedBlock().getLocation());

        // find out which corner was clicked
        ThinLocation[] allCorners =
            {
                new ThinLocation(zoneCorners[0].getBlockX(), zoneCorners[0].getBlockY(), zoneCorners[0].getBlockZ()),
                new ThinLocation(zoneCorners[1].getBlockX(), zoneCorners[0].getBlockY(), zoneCorners[0].getBlockZ()),
                new ThinLocation(zoneCorners[0].getBlockX(), zoneCorners[0].getBlockY(), zoneCorners[1].getBlockZ()),
                new ThinLocation(zoneCorners[1].getBlockX(), zoneCorners[0].getBlockY(), zoneCorners[1].getBlockZ()),
            };

        int changedCorner = -1;

        for (int i = 0; i < allCorners.length; i++)
        {
            if (allCorners[i].getBlockX() == firstClick.getBlockX() && allCorners[i].getBlockZ() == firstClick.getBlockZ())
            {
                changedCorner = i;
            }
        }

        ThinLocation newLesserCorner = new ThinLocation(zoneCorners[0].getBlockX(), zoneCorners[0].getBlockY(), zoneCorners[0].getBlockZ());
        ThinLocation newGreaterCorner = new ThinLocation(zoneCorners[1].getBlockX(), zoneCorners[1].getBlockY(), zoneCorners[1].getBlockZ());

        if (changedCorner == 0)
        {
            newLesserCorner = new ThinLocation(secondClick.getBlockX(), newLesserCorner.getBlockY(), secondClick.getBlockZ());
        }

        if (changedCorner == 1)
        {
            newLesserCorner = new ThinLocation(newLesserCorner.getBlockX(), newLesserCorner.getBlockY(), secondClick.getBlockZ());
            newGreaterCorner = new ThinLocation(secondClick.getBlockX(), newGreaterCorner.getBlockY(), newGreaterCorner.getBlockZ());
        }

        if (changedCorner == 2)
        {
            newLesserCorner = new ThinLocation(secondClick.getBlockX(), newLesserCorner.getBlockY(), newLesserCorner.getBlockZ());
            newGreaterCorner = new ThinLocation(newGreaterCorner.getBlockX(), newGreaterCorner.getBlockY(), secondClick.getBlockZ());
        }

        if (changedCorner == 3)
        {
            newGreaterCorner = new ThinLocation(secondClick.getBlockX(), newGreaterCorner.getBlockY(), secondClick.getBlockZ());
        }

        // check for primary vs primary overlap
        for (SafeCityZone z : context.getZones())
        {
            if (scPlayer.getZoneManager().getResizeZone().getId() == z.getId())
            {
                continue;
            }

            if (context.isOverlap(newLesserCorner, newGreaterCorner, z.getLesserCorner(), z.getGreaterCorner()))
            {
                scPlayer.getZoneManager().clearResizeData();
                scPlayer.startCoolDown(2L);

                scPlayer.getBukkitPlayer().sendMessage(ChatColor.RED + "Primary zone is overlapping another primary zone.");
                return;
            }
        }

        // check for sub-zones now outside primary zone
        for (SafeCitySubZone s : scPlayer.getZoneManager().getResizeZone().getChildren())
        {
            if (subZoneOutside(newLesserCorner, newGreaterCorner, s.getLesserCorner(), s.getGreaterCorner()))
            {
                scPlayer.getZoneManager().clearResizeData();
                scPlayer.startCoolDown(2L);

                scPlayer.getBukkitPlayer().sendMessage(ChatColor.RED + "One or more of your subzones is outside of the modified area.");
                return;
            }
        }

        int width = (newGreaterCorner.getBlockX() - newLesserCorner.getBlockX()) + 1;
        int length = (newGreaterCorner.getBlockZ() - newLesserCorner.getBlockZ()) + 1;

        // check minimum size
        if (width < 5 || length < 5)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Too_Small());

            scPlayer.getZoneManager().clearResizeData();
            scPlayer.startCoolDown(2L);
            return;
        }

        int newSize = (width * length);

        boolean isSmaller = (newSize < oldSize);

        int sizeDiff = Math.abs((newSize - oldSize));

        int newCount = scPlayer.getZoneManager().getResizeZone().getAvailableBlocks();

        if (isSmaller)
        {
            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Resized_Smaller(sizeDiff));
            newCount += (oldSize - newSize);
        }
        else
        {
            int checkBlockCount = (scPlayer.getZoneManager().getResizeZone().getAvailableBlocks() - sizeDiff);

            if (checkBlockCount < 0)
            {
                int blocksRequired = Math.abs(checkBlockCount);

                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Blocks_Needed(blocksRequired));

                scPlayer.getZoneManager().clearResizeData();
                scPlayer.startCoolDown(2L);
                return;
            }

            scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Zone_Resized_Larger(sizeDiff));
            newCount -= (newSize - oldSize);
        }

        // remove old chunk pairs and replace with new ones BEFORE committing because right now we have both.
        context.removeZoneFromMap(scPlayer.getZoneManager().getResizeZone());

        scPlayer.getZoneManager().getResizeZone().defineCorners(newLesserCorner, newGreaterCorner);

        context.addNewZoneToMap(scPlayer.getZoneManager().getResizeZone());


        scPlayer.getZoneManager().getResizeZone().setAvailableBlocks(newCount);
        scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().Blocks_Remaining(scPlayer.getZoneManager().getResizeZone().getAvailableBlocks()));
    }

    private void resizeSubZone(PlayerInteractEvent event, SafeCityPlayer scPlayer)
    {
        ThinLocation[] zoneCorners = scPlayer.getZoneManager().getResizeSubZone().getCorners();
        zoneCorners = context.sortCorners(zoneCorners[0], zoneCorners[1]);

        boolean isFullSize = (zoneCorners[0].getBlockY() == 1 && zoneCorners[1].getBlockY() == (scPlayer.getBukkitPlayer().getWorld().getMaxHeight() -1));

        ThinLocation firstClick = scPlayer.getZoneManager().getResizeCornerChanged();
		ThinLocation secondClick = context.toThinLocation(event.getClickedBlock().getLocation());

        // find out which corner was clicked
        ThinLocation[] allCorners =
        {
            new ThinLocation(zoneCorners[0].getBlockX(), zoneCorners[0].getBlockY(), zoneCorners[0].getBlockZ()),
            new ThinLocation(zoneCorners[1].getBlockX(), zoneCorners[0].getBlockY(), zoneCorners[0].getBlockZ()),
            new ThinLocation(zoneCorners[0].getBlockX(), zoneCorners[0].getBlockY(), zoneCorners[1].getBlockZ()),
            new ThinLocation(zoneCorners[1].getBlockX(), zoneCorners[0].getBlockY(), zoneCorners[1].getBlockZ()),

            new ThinLocation(zoneCorners[0].getBlockX(), zoneCorners[1].getBlockY(), zoneCorners[0].getBlockZ()),
            new ThinLocation(zoneCorners[1].getBlockX(), zoneCorners[1].getBlockY(), zoneCorners[0].getBlockZ()),
            new ThinLocation(zoneCorners[0].getBlockX(), zoneCorners[1].getBlockY(), zoneCorners[1].getBlockZ()),
            new ThinLocation(zoneCorners[1].getBlockX(), zoneCorners[1].getBlockY(), zoneCorners[1].getBlockZ()),
        };

        int changedCorner = -1;

        for (int i = 0; i < allCorners.length; i++)
        {
            // ignore higher co-ords
            if (isFullSize && i > 3)
            {
                continue;
            }

            if (isFullSize)
            {
                if (allCorners[i].getBlockX() == firstClick.getBlockX() && allCorners[i].getBlockZ() == firstClick.getBlockZ())
                {
                    changedCorner = i;
                }
            }
            else
            {
                if (allCorners[i].getBlockX() == firstClick.getBlockX() && allCorners[i].getBlockY() == firstClick.getBlockY() && allCorners[i].getBlockZ() == firstClick.getBlockZ())
                {
                    changedCorner = i;
                }
            }
        }

        ThinLocation newLesserCorner = new ThinLocation(zoneCorners[0].getBlockX(), zoneCorners[0].getBlockY(), zoneCorners[0].getBlockZ());
        ThinLocation newGreaterCorner = new ThinLocation(zoneCorners[1].getBlockX(), zoneCorners[1].getBlockY(), zoneCorners[1].getBlockZ());

        if (changedCorner == 0)
        {
            newLesserCorner = new ThinLocation(secondClick.getBlockX(), (isFullSize) ? newLesserCorner.getBlockY() : secondClick.getBlockY(), secondClick.getBlockZ());
        }

        if (changedCorner == 1)
        {
            newLesserCorner = new ThinLocation(newLesserCorner.getBlockX(), (isFullSize) ? newLesserCorner.getBlockY() : secondClick.getBlockY(), secondClick.getBlockZ());
            newGreaterCorner = new ThinLocation(secondClick.getBlockX(), newGreaterCorner.getBlockY(), newGreaterCorner.getBlockZ());
        }

        if (changedCorner == 2)
        {
            newLesserCorner = new ThinLocation(secondClick.getBlockX(), (isFullSize) ? newLesserCorner.getBlockY() : secondClick.getBlockY(), newLesserCorner.getBlockZ());
            newGreaterCorner = new ThinLocation(newGreaterCorner.getBlockX(), newGreaterCorner.getBlockY(), secondClick.getBlockZ());
        }

        if (changedCorner == 3)
        {
            newGreaterCorner = new ThinLocation(secondClick.getBlockX(), newGreaterCorner.getBlockY(), secondClick.getBlockZ());

            if (!isFullSize)
            {
                newLesserCorner = new ThinLocation(newLesserCorner.getBlockX(), secondClick.getBlockY(), newLesserCorner.getBlockZ());
            }
        }

        if (changedCorner == 4)
        {
            newLesserCorner = new ThinLocation(secondClick.getBlockX(), newLesserCorner.getBlockY(), secondClick.getBlockZ());

            if (!isFullSize)
            {
                newGreaterCorner = new ThinLocation(newGreaterCorner.getBlockX(), secondClick.getBlockY(), newGreaterCorner.getBlockZ());
            }
        }

        if (changedCorner == 5)
        {
            newLesserCorner = new ThinLocation(newLesserCorner.getBlockX(), newLesserCorner.getBlockY(), secondClick.getBlockZ());
            newGreaterCorner = new ThinLocation(secondClick.getBlockX(), (isFullSize) ? newGreaterCorner.getBlockY() : secondClick.getBlockY(), newGreaterCorner.getBlockZ());
        }

        if (changedCorner == 6)
        {
            newLesserCorner = new ThinLocation(secondClick.getBlockX(), newLesserCorner.getBlockY(), newLesserCorner.getBlockZ());
            newGreaterCorner = new ThinLocation(newGreaterCorner.getBlockX(), (isFullSize) ? newGreaterCorner.getBlockY() : secondClick.getBlockY(), secondClick.getBlockZ());
        }

        if (changedCorner == 7)
        {
            newGreaterCorner = new ThinLocation(secondClick.getBlockX(), (isFullSize) ? newGreaterCorner.getBlockY() : secondClick.getBlockY(), secondClick.getBlockZ());
        }

        SafeCityZone zone = scPlayer.getZoneManager().getResizeSubZone().getParentZone();

        // check if subzone was moved outside of zone
        if (subZoneOutside(zone.getLesserCorner(), zone.getGreaterCorner(), newLesserCorner, newGreaterCorner))
        {
            scPlayer.getZoneManager().clearResizeData();
            scPlayer.startCoolDown(2L);

            scPlayer.getBukkitPlayer().sendMessage(ChatColor.RED + "Sub-zone is outside of the primary zone area.");
            return;
        }

        // check if subzone overlaps another subzone
        for (SafeCitySubZone s : zone.getChildren())
        {
            if (scPlayer.getZoneManager().getResizeSubZone().getId() == s.getId())
            {
                continue;
            }

            if (context.isOverlap(newLesserCorner, newGreaterCorner, s.getLesserCorner(), s.getGreaterCorner()))
            {
                scPlayer.getZoneManager().clearResizeData();
                scPlayer.startCoolDown(2L);

                scPlayer.getBukkitPlayer().sendMessage(ChatColor.RED + "Sub-zone is overlapping another sub-zone.");
                return;
            }
        }

        int width = (newGreaterCorner.getBlockX() - newLesserCorner.getBlockX()) + 1;
        int length = (newGreaterCorner.getBlockZ() - newLesserCorner.getBlockZ()) + 1;

        if (!isFullSize)
        {
            if (width < 5 || length < 5)
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().SubZone_Too_Small());

                scPlayer.getZoneManager().clearResizeData();
                scPlayer.startCoolDown(2L);
                return;
            }
        }
        else
        {
            int height = (newGreaterCorner.getBlockY() - newLesserCorner.getBlockY()) + 1;

            if (width < 5 || length < 5 || height < 3)
            {
                scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().SubZone3D_Too_Small());

                scPlayer.getZoneManager().clearResizeData();
                scPlayer.startCoolDown(2L);
                return;
            }
        }

        context.removeSubZoneFromMap(scPlayer.getZoneManager().getResizeSubZone());

        scPlayer.getZoneManager().getResizeSubZone().defineCorners(newLesserCorner, newGreaterCorner);

        context.addNewSubZoneToMap(scPlayer.getZoneManager().getResizeSubZone());

        scPlayer.getBukkitPlayer().sendMessage(context.getMessageHandler().SubZone_Resized(scPlayer.getZoneManager().getResizeSubZone().getName()));

    }
}